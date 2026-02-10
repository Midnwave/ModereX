/**
 * ModereX Link Page
 * 4-step flow: Code Entry → Identity Confirm → Password (new) → Success
 */
(function() {
    'use strict';

    const GATEWAY_URL = 'https://gateway.moderex.net';
    const PANEL_URL = 'https://panel.moderex.net';

    // State
    let currentStep = 1;
    let verifiedData = null; // { uuid, username, hasAccount, codeHash }

    // DOM elements
    const digits = document.querySelectorAll('.code-digit');
    const verifyBtn = document.getElementById('verify-btn');
    const codeError = document.getElementById('code-error');
    const loading = document.getElementById('loading-overlay');
    const loadingText = document.getElementById('loading-text');

    // ================================================================
    // Step 1: Code Entry
    // ================================================================

    digits.forEach((input, idx) => {
        input.addEventListener('input', (e) => {
            const val = e.target.value.replace(/\D/g, '');
            e.target.value = val;

            if (val) {
                e.target.classList.add('filled');
                // Auto-advance to next digit
                if (idx < digits.length - 1) {
                    digits[idx + 1].focus();
                }
            } else {
                e.target.classList.remove('filled');
            }

            updateVerifyButton();
            clearError('code-error');
        });

        input.addEventListener('keydown', (e) => {
            // Backspace: clear and go back
            if (e.key === 'Backspace' && !e.target.value && idx > 0) {
                digits[idx - 1].focus();
                digits[idx - 1].value = '';
                digits[idx - 1].classList.remove('filled');
                updateVerifyButton();
            }
            // Arrow keys
            if (e.key === 'ArrowLeft' && idx > 0) {
                e.preventDefault();
                digits[idx - 1].focus();
            }
            if (e.key === 'ArrowRight' && idx < digits.length - 1) {
                e.preventDefault();
                digits[idx + 1].focus();
            }
        });

        // Paste support
        input.addEventListener('paste', (e) => {
            e.preventDefault();
            const pasted = (e.clipboardData.getData('text') || '').replace(/\D/g, '');
            if (pasted.length >= 10) {
                for (let i = 0; i < 10 && i < digits.length; i++) {
                    digits[i].value = pasted[i];
                    digits[i].classList.add('filled');
                }
                digits[Math.min(9, digits.length - 1)].focus();
                updateVerifyButton();
            }
        });
    });

    function updateVerifyButton() {
        const code = getCodeValue();
        verifyBtn.disabled = code.length !== 10;
    }

    function getCodeValue() {
        return Array.from(digits).map(d => d.value).join('');
    }

    verifyBtn.addEventListener('click', async () => {
        const code = getCodeValue();
        if (code.length !== 10) return;

        showLoading('Verifying code...');

        try {
            const resp = await fetch(GATEWAY_URL + '/api/link/verify', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code })
            });

            // Clear code from inputs immediately (security: don't keep it in DOM)
            digits.forEach(d => { d.value = ''; d.classList.remove('filled'); });

            const data = await resp.json();

            if (!resp.ok) {
                hideLoading();
                showError('code-error', data.error || 'Verification failed');
                digits.forEach(d => d.classList.add('error'));
                setTimeout(() => digits.forEach(d => d.classList.remove('error')), 400);
                digits[0].focus();
                return;
            }

            verifiedData = data;
            hideLoading();
            goToStep(2);
        } catch (e) {
            hideLoading();
            showError('code-error', 'Connection failed. Is the gateway online?');
        }
    });

    // ================================================================
    // Step 2: Identity Confirmation
    // ================================================================

    document.getElementById('confirm-yes').addEventListener('click', () => {
        if (verifiedData.hasAccount) {
            // Existing user — auto-login (skip password)
            autoLoginExistingUser();
        } else {
            // New user — go to password creation
            goToStep(3);
        }
    });

    document.getElementById('confirm-no').addEventListener('click', () => {
        showError('identity-error', 'If this is not you, run /mx link again with your account.');
    });

    async function autoLoginExistingUser() {
        showLoading('Logging in...');
        try {
            const fingerprint = await getDeviceFingerprint();
            const resp = await fetch(GATEWAY_URL + '/api/link/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    codeHash: verifiedData.codeHash,
                    fingerprintHash: fingerprint
                })
            });

            const data = await resp.json();
            if (!resp.ok) {
                hideLoading();
                showError('identity-error', data.error || 'Login failed');
                return;
            }

            // Save session
            saveSession(data.sessionToken, data.uuid, data.username);

            hideLoading();
            document.getElementById('success-title').textContent = 'Welcome Back!';
            document.getElementById('success-text').textContent = 'You\'re logged in as ' + data.username + '.';
            goToStep(4);
        } catch (e) {
            hideLoading();
            showError('identity-error', 'Connection failed');
        }
    }

    // ================================================================
    // Step 3: Password Creation
    // ================================================================

    const passwordInput = document.getElementById('password');
    const confirmInput = document.getElementById('password-confirm');
    const strengthBar = document.getElementById('strength-bar');
    const strengthLabel = document.getElementById('strength-label');
    const createBtn = document.getElementById('create-account-btn');

    passwordInput.addEventListener('input', () => {
        updateStrength();
        validatePasswords();
    });

    confirmInput.addEventListener('input', validatePasswords);

    // Toggle password visibility
    document.querySelectorAll('.toggle-password').forEach(btn => {
        btn.addEventListener('click', () => {
            const target = document.getElementById(btn.dataset.target);
            const isPassword = target.type === 'password';
            target.type = isPassword ? 'text' : 'password';
            btn.querySelector('i').className = isPassword ? 'fas fa-eye-slash' : 'fas fa-eye';
        });
    });

    function updateStrength() {
        const pw = passwordInput.value;
        let score = 0;

        if (pw.length >= 8) score += 20;
        if (pw.length >= 12) score += 10;
        if (pw.length >= 16) score += 10;
        if (/[a-z]/.test(pw)) score += 10;
        if (/[A-Z]/.test(pw)) score += 15;
        if (/[0-9]/.test(pw)) score += 15;
        if (/[^a-zA-Z0-9]/.test(pw)) score += 20;

        // Penalize common patterns
        if (/^(password|12345|qwerty|abc123)/i.test(pw)) score = Math.min(score, 10);
        if (/(.)\1{3,}/.test(pw)) score -= 10;

        score = Math.max(0, Math.min(100, score));

        strengthBar.style.width = score + '%';

        if (score < 25) {
            strengthBar.style.background = '#ef4444';
            strengthLabel.textContent = 'Weak';
            strengthLabel.style.color = '#ef4444';
        } else if (score < 50) {
            strengthBar.style.background = '#f59e0b';
            strengthLabel.textContent = 'Fair';
            strengthLabel.style.color = '#f59e0b';
        } else if (score < 75) {
            strengthBar.style.background = '#2d7aed';
            strengthLabel.textContent = 'Good';
            strengthLabel.style.color = '#2d7aed';
        } else {
            strengthBar.style.background = '#22c55e';
            strengthLabel.textContent = 'Strong';
            strengthLabel.style.color = '#22c55e';
        }

        return score;
    }

    function validatePasswords() {
        const pw = passwordInput.value;
        const confirm = confirmInput.value;
        const score = updateStrength();

        clearError('password-error');

        if (pw.length > 0 && pw.length < 8) {
            showError('password-error', 'Password must be at least 8 characters');
            createBtn.disabled = true;
            return;
        }

        if (confirm.length > 0 && pw !== confirm) {
            showError('password-error', 'Passwords do not match');
            createBtn.disabled = true;
            return;
        }

        createBtn.disabled = !(pw.length >= 8 && pw === confirm && score >= 75);
    }

    createBtn.addEventListener('click', async () => {
        const pw = passwordInput.value;
        if (pw.length < 8 || pw !== confirmInput.value) return;

        showLoading('Creating account...');

        try {
            const fingerprint = await getDeviceFingerprint();
            const resp = await fetch(GATEWAY_URL + '/api/link/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    codeHash: verifiedData.codeHash,
                    password: pw,
                    fingerprintHash: fingerprint
                })
            });

            // Clear password from inputs immediately
            passwordInput.value = '';
            confirmInput.value = '';

            const data = await resp.json();
            if (!resp.ok) {
                hideLoading();
                showError('password-error', data.error || 'Registration failed');
                return;
            }

            // Save session
            saveSession(data.sessionToken, data.uuid, data.username);

            hideLoading();
            document.getElementById('success-title').textContent = 'Account Created!';
            document.getElementById('success-text').textContent = 'Welcome, ' + data.username + '! Your account is ready.';
            goToStep(4);
        } catch (e) {
            hideLoading();
            showError('password-error', 'Connection failed');
        }
    });

    // ================================================================
    // Step Navigation
    // ================================================================

    function goToStep(step) {
        // Hide all steps
        document.querySelectorAll('.step-content').forEach(el => el.classList.add('hidden'));

        // Show target step
        document.getElementById('step-' + step).classList.remove('hidden');

        // Update progress bar
        document.querySelectorAll('.progress-step').forEach(el => {
            const s = parseInt(el.dataset.step);
            el.classList.remove('active', 'completed');
            if (s === step) el.classList.add('active');
            else if (s < step) el.classList.add('completed');
        });

        // Fill progress lines
        document.querySelectorAll('.progress-line').forEach((line, idx) => {
            line.classList.toggle('filled', idx < step - 1);
        });

        currentStep = step;

        // Step-specific setup
        if (step === 2 && verifiedData) {
            setupIdentityStep();
        }
    }

    function setupIdentityStep() {
        const playerHead = document.getElementById('player-head');
        const identityText = document.getElementById('identity-text');

        // Load player head (crafatar with minotar fallback for Geyser)
        playerHead.src = 'https://crafatar.com/avatars/' + verifiedData.uuid + '?overlay&size=128';
        playerHead.onerror = function() {
            this.src = 'https://minotar.net/helm/' + verifiedData.username + '/128';
            this.onerror = null;
        };

        if (verifiedData.hasAccount) {
            identityText.innerHTML = '<p class="welcome-back">Welcome Back!</p><h2><span class="player-name">' +
                escapeHtml(verifiedData.username) + '</span></h2><p>Click below to sign in</p>';
            document.getElementById('confirm-yes').textContent = 'Sign In';
            document.getElementById('confirm-no').style.display = 'none';
        } else {
            identityText.innerHTML = '<p>Are you</p><h2><span class="player-name">' +
                escapeHtml(verifiedData.username) + '</span>?</h2>';
            document.getElementById('confirm-yes').textContent = "Yes, that's me!";
            document.getElementById('confirm-no').style.display = '';
        }
    }

    // ================================================================
    // Utilities
    // ================================================================

    function showLoading(text) {
        loadingText.textContent = text || 'Loading...';
        loading.classList.remove('hidden');
    }

    function hideLoading() {
        loading.classList.add('hidden');
    }

    function showError(id, msg) {
        document.getElementById(id).textContent = msg;
    }

    function clearError(id) {
        document.getElementById(id).textContent = '';
    }

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    async function getDeviceFingerprint() {
        try {
            const components = [
                navigator.userAgent,
                screen.width + 'x' + screen.height,
                screen.colorDepth,
                Intl.DateTimeFormat().resolvedOptions().timeZone,
                navigator.hardwareConcurrency || 0,
                navigator.platform || ''
            ].join('|');

            const encoder = new TextEncoder();
            const data = encoder.encode(components);
            const hashBuffer = await crypto.subtle.digest('SHA-256', data);
            const hashArray = Array.from(new Uint8Array(hashBuffer));
            return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
        } catch (e) {
            return null;
        }
    }

    function saveSession(token, uuid, username) {
        try {
            localStorage.setItem('mx_session', JSON.stringify({
                token,
                uuid,
                username,
                savedAt: Date.now()
            }));
        } catch (e) {}
    }

    // Auto-focus first digit on load
    if (digits[0]) digits[0].focus();
})();
