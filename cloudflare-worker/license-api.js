/**
 * ModereX License Validation API - Cloudflare Worker
 * Deployed at: license.moderex.net
 *
 * Environment Variables Required:
 * - PRIVATE_KEY: RSA private key (PEM format) for signing responses
 * - ADMIN_SECRET: Secret for admin endpoints (UUID recommended)
 *
 * KV Namespace Required:
 * - LICENSE_TOKENS: Stores license token data
 */

// Rate limiting storage (in-memory, resets on worker restart)
const rateLimits = new Map();

// Helper: Generate RSA signature
async function signResponse(data, privateKeyPem) {
  try {
    const privateKey = await crypto.subtle.importKey(
      'pkcs8',
      pemToArrayBuffer(privateKeyPem),
      {
        name: 'RSASSA-PKCS1-v1_5',
        hash: 'SHA-256',
      },
      false,
      ['sign']
    );

    const encoder = new TextEncoder();
    const dataBuffer = encoder.encode(JSON.stringify(data));

    const signature = await crypto.subtle.sign(
      'RSASSA-PKCS1-v1_5',
      privateKey,
      dataBuffer
    );

    return arrayBufferToBase64(signature);
  } catch (error) {
    console.error('Signature generation failed:', error);
    return null;
  }
}

// Helper: Convert PEM to ArrayBuffer
function pemToArrayBuffer(pem) {
  const b64 = pem
    .replace(/-----BEGIN PRIVATE KEY-----/, '')
    .replace(/-----END PRIVATE KEY-----/, '')
    .replace(/\s/g, '');
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

// Helper: ArrayBuffer to Base64
function arrayBufferToBase64(buffer) {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}

// Helper: Check rate limit
function checkRateLimit(key, maxRequests = 10, windowMs = 60000) {
  const now = Date.now();
  const record = rateLimits.get(key) || { count: 0, resetAt: now + windowMs };

  if (now > record.resetAt) {
    record.count = 0;
    record.resetAt = now + windowMs;
  }

  record.count++;
  rateLimits.set(key, record);

  return record.count <= maxRequests;
}

// Helper: Verify admin secret
function verifyAdminAuth(request, env) {
  const authHeader = request.headers.get('X-Admin-Secret');
  return authHeader === env.ADMIN_SECRET;
}

// Helper: CORS headers
function corsHeaders() {
  return {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, X-Admin-Secret',
  };
}

// Endpoint: POST /validate - Validate license token
async function handleValidate(request, env) {
  try {
    const body = await request.json();
    const { token, serverId, serverName, version } = body;

    if (!token) {
      return new Response(JSON.stringify({ valid: false, error: 'Missing token' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Rate limiting per token
    if (!checkRateLimit(`validate:${token}`, 10, 60000)) {
      return new Response(JSON.stringify({ valid: false, error: 'Rate limit exceeded' }), {
        status: 429,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Fetch license data from KV
    const licenseData = await env.LICENSE_TOKENS.get(token, { type: 'json' });

    if (!licenseData) {
      return new Response(JSON.stringify({ valid: false, error: 'Invalid token' }), {
        status: 404,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Check if active
    if (!licenseData.active) {
      return new Response(JSON.stringify({ valid: false, error: 'License revoked' }), {
        status: 403,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Check expiry
    const now = Date.now();
    if (licenseData.expiresAt && now > licenseData.expiresAt) {
      return new Response(JSON.stringify({ valid: false, error: 'License expired' }), {
        status: 403,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Check server binding (if set)
    if (licenseData.serverId && licenseData.serverId !== serverId) {
      return new Response(JSON.stringify({ valid: false, error: 'Token not authorized for this server' }), {
        status: 403,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Build response data
    const responseData = {
      valid: true,
      expiresAt: licenseData.expiresAt || null,
      features: licenseData.features || {},
      timestamp: now,
    };

    // Sign the response
    const signature = await signResponse(responseData, env.PRIVATE_KEY);

    if (!signature) {
      return new Response(JSON.stringify({ valid: false, error: 'Signature generation failed' }), {
        status: 500,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Update last validation timestamp
    licenseData.lastValidated = now;
    licenseData.lastServerId = serverId;
    licenseData.lastServerName = serverName;
    licenseData.lastVersion = version;
    await env.LICENSE_TOKENS.put(token, JSON.stringify(licenseData));

    return new Response(JSON.stringify({ ...responseData, signature }), {
      status: 200,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  } catch (error) {
    console.error('Validation error:', error);
    return new Response(JSON.stringify({ valid: false, error: 'Internal server error' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }
}

// Endpoint: POST /heartbeat - Update license heartbeat
async function handleHeartbeat(request, env) {
  try {
    const body = await request.json();
    const { token, serverId, players, uptime } = body;

    if (!token) {
      return new Response(JSON.stringify({ stillValid: false, error: 'Missing token' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Fetch license data
    const licenseData = await env.LICENSE_TOKENS.get(token, { type: 'json' });

    if (!licenseData || !licenseData.active) {
      return new Response(JSON.stringify({ stillValid: false, error: 'License invalid or revoked' }), {
        status: 403,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Check expiry
    const now = Date.now();
    if (licenseData.expiresAt && now > licenseData.expiresAt) {
      return new Response(JSON.stringify({ stillValid: false, error: 'License expired' }), {
        status: 403,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    // Update heartbeat data
    licenseData.lastHeartbeat = now;
    licenseData.lastServerId = serverId;
    licenseData.lastPlayers = players;
    licenseData.lastUptime = uptime;
    await env.LICENSE_TOKENS.put(token, JSON.stringify(licenseData));

    return new Response(JSON.stringify({ stillValid: true, timestamp: now }), {
      status: 200,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  } catch (error) {
    console.error('Heartbeat error:', error);
    return new Response(JSON.stringify({ stillValid: false, error: 'Internal server error' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }
}

// Endpoint: POST /admin/create - Create new license token
async function handleAdminCreate(request, env) {
  if (!verifyAdminAuth(request, env)) {
    return new Response(JSON.stringify({ error: 'Unauthorized' }), {
      status: 401,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }

  try {
    const body = await request.json();
    const { token: providedToken, maxServers = 1, expiresAt = null, note = '', createdBy = 'admin' } = body;

    // Use provided token or generate new one
    const token = providedToken || crypto.randomUUID();
    const now = Date.now();

    const licenseData = {
      token,
      serverId: null, // Not bound to specific server yet
      active: true,
      createdAt: now,
      createdBy,
      expiresAt,
      maxServers,
      note,
      features: {},
      lastValidated: null,
      lastHeartbeat: null,
      lastServerId: null,
      lastServerName: null,
      lastVersion: null,
      lastPlayers: 0,
      lastUptime: 0,
    };

    await env.LICENSE_TOKENS.put(token, JSON.stringify(licenseData));

    return new Response(JSON.stringify({ success: true, token, data: licenseData }), {
      status: 200,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  } catch (error) {
    console.error('Create license error:', error);
    return new Response(JSON.stringify({ error: 'Internal server error' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }
}

// Endpoint: POST /admin/revoke - Revoke license token
async function handleAdminRevoke(request, env) {
  if (!verifyAdminAuth(request, env)) {
    return new Response(JSON.stringify({ error: 'Unauthorized' }), {
      status: 401,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }

  try {
    const body = await request.json();
    const { token } = body;

    if (!token) {
      return new Response(JSON.stringify({ error: 'Missing token' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    const licenseData = await env.LICENSE_TOKENS.get(token, { type: 'json' });

    if (!licenseData) {
      return new Response(JSON.stringify({ error: 'Token not found' }), {
        status: 404,
        headers: { 'Content-Type': 'application/json', ...corsHeaders() },
      });
    }

    licenseData.active = false;
    licenseData.revokedAt = Date.now();
    await env.LICENSE_TOKENS.put(token, JSON.stringify(licenseData));

    return new Response(JSON.stringify({ success: true, message: 'License revoked' }), {
      status: 200,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  } catch (error) {
    console.error('Revoke license error:', error);
    return new Response(JSON.stringify({ error: 'Internal server error' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }
}

// Endpoint: GET /admin/list - List all licenses
async function handleAdminList(request, env) {
  if (!verifyAdminAuth(request, env)) {
    return new Response(JSON.stringify({ error: 'Unauthorized' }), {
      status: 401,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }

  try {
    const list = await env.LICENSE_TOKENS.list();
    const licenses = [];

    for (const key of list.keys) {
      const data = await env.LICENSE_TOKENS.get(key.name, { type: 'json' });
      if (data) {
        licenses.push(data);
      }
    }

    return new Response(JSON.stringify({ licenses }), {
      status: 200,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  } catch (error) {
    console.error('List licenses error:', error);
    return new Response(JSON.stringify({ error: 'Internal server error' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  }
}

// Main request handler
export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const path = url.pathname;

    // Handle OPTIONS for CORS
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        status: 204,
        headers: corsHeaders(),
      });
    }

    // Route requests
    if (path === '/validate' && request.method === 'POST') {
      return handleValidate(request, env);
    }

    if (path === '/heartbeat' && request.method === 'POST') {
      return handleHeartbeat(request, env);
    }

    if (path === '/admin/create' && request.method === 'POST') {
      return handleAdminCreate(request, env);
    }

    if (path === '/admin/revoke' && request.method === 'POST') {
      return handleAdminRevoke(request, env);
    }

    if (path === '/admin/list' && request.method === 'GET') {
      return handleAdminList(request, env);
    }

    // 404 for unknown routes
    return new Response(JSON.stringify({ error: 'Not found' }), {
      status: 404,
      headers: { 'Content-Type': 'application/json', ...corsHeaders() },
    });
  },
};
