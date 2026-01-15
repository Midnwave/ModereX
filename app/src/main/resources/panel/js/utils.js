/* ============================================
   ModereX Control Panel - Utilities
   ============================================ */
(function() {
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => [...root.querySelectorAll(sel)];
  const clamp = (n, a, b) => Math.max(a, Math.min(b, n));
  const rnd = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;
  const pick = arr => arr[Math.floor(Math.random() * arr.length)];
  const now = () => Date.now();
  const pad2 = n => String(n).padStart(2, '0');

  const fmtClock = (ms = Date.now()) => {
    const d = new Date(ms);
    let h = d.getHours();
    const ampm = h >= 12 ? 'PM' : 'AM';
    h = h % 12 || 12;
    return `${h}:${pad2(d.getMinutes())} ${ampm}`;
  };

  const fmtShort = (ms = Date.now()) => {
    const d = new Date(ms);
    let h = d.getHours();
    const ampm = h >= 12 ? 'PM' : 'AM';
    h = h % 12 || 12;
    return `${d.getMonth() + 1}/${d.getDate()} ${h}:${pad2(d.getMinutes())} ${ampm}`;
  };

  const fmtLong = (ms = Date.now()) => {
    const d = new Date(ms);
    let h = d.getHours();
    const ampm = h >= 12 ? 'PM' : 'AM';
    h = h % 12 || 12;
    return `${d.getMonth() + 1}/${d.getDate()}/${d.getFullYear()} ${h}:${pad2(d.getMinutes())} ${ampm}`;
  };

  let uidCounter = 0;
  const uid = (prefix = 'id') => `${prefix}_${Date.now().toString(36)}_${(uidCounter++).toString(36)}_${Math.random().toString(36).slice(2, 6)}`;

  const escapeHtml = str => {
    if (str == null) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  };

  const parseDurationToMs = (dur, start) => {
    if (!dur || dur === 'perm' || dur === 'permanent') return null;
    const m = dur.match(/^(\d+)\s*([smhdw])$/);
    if (!m) return null;
    const n = parseInt(m[1], 10);
    const mult = { 's': 1000, 'm': 60000, 'h': 3600000, 'd': 86400000, 'w': 604800000 }[m[2]] || 1000;
    return start + (n * mult);
  };

  const avatarUrl = (player) => {
    const n = encodeURIComponent(player?.name || 'Steve');
    return `https://mc-heads.net/avatar/${n}/64`;
  };

  // Export to window
  window.MX = window.MX || {};
  window.MX.utils = {
    $, $$, clamp, rnd, pick, now, pad2, fmtClock, fmtShort, fmtLong, uid, escapeHtml, parseDurationToMs, avatarUrl
  };
})();
