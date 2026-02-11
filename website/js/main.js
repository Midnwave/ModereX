// ============================================
// ModereX Landing Page - Enhanced JavaScript
// ============================================

const GATEWAY_URL = 'https://neighbors-steps-unable-stop.trycloudflare.com';
const SESSION_KEY = 'mx_website_session';
const TURNSTILE_SITE_KEY = '1x00000000000000000000AA'; // Cloudflare test key (always passes) — replace with real key for production

let allReviews = [];
let showingAllReviews = false;
let selectedRating = 0;
let turnstileToken = null;
let turnstileWidgetId = null;

document.addEventListener('DOMContentLoaded', () => {
    initNavbar();
    initScrollAnimations();
    initMobileMenu();
    initSmoothScroll();
    fetchLiveStats();
    initTypingAnimation();
    initCountUpAnimations();
    initButtonEffects();
    initPanelMockAnimations();
    initAuth();
    initReviews();
});

// ============================================
// Navbar Enhancement
// ============================================

function initNavbar() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
}

// ============================================
// Mobile Menu
// ============================================

function initMobileMenu() {
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');

    if (!mobileMenuBtn || !navLinks) return;

    mobileMenuBtn.addEventListener('click', () => {
        navLinks.classList.toggle('mobile-open');
        const icon = mobileMenuBtn.querySelector('i');

        if (navLinks.classList.contains('mobile-open')) {
            icon.classList.remove('fa-bars');
            icon.classList.add('fa-times');
        } else {
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        }
    });

    // Close menu when clicking a link
    navLinks.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('mobile-open');
            const icon = mobileMenuBtn.querySelector('i');
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        });
    });

    // Close menu when clicking outside
    document.addEventListener('click', (e) => {
        if (!navLinks.contains(e.target) && !mobileMenuBtn.contains(e.target)) {
            navLinks.classList.remove('mobile-open');
            const icon = mobileMenuBtn.querySelector('i');
            icon.classList.remove('fa-times');
            icon.classList.add('fa-bars');
        }
    });
}

// ============================================
// Smooth Scroll
// ============================================

function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');

            if (targetId === '#') return;

            const target = document.querySelector(targetId);
            if (!target) return;

            const navbar = document.querySelector('.navbar');
            const navHeight = navbar ? navbar.offsetHeight : 0;
            const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - navHeight - 20;

            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        });
    });

    // Scroll indicator in hero
    const scrollIndicator = document.querySelector('.scroll-indicator');
    if (scrollIndicator) {
        scrollIndicator.addEventListener('click', () => {
            const featuresSection = document.querySelector('#features');
            if (featuresSection) {
                const navbar = document.querySelector('.navbar');
                const navHeight = navbar ? navbar.offsetHeight : 0;
                const targetPosition = featuresSection.getBoundingClientRect().top + window.pageYOffset - navHeight;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    }
}

// ============================================
// Scroll Animations
// ============================================

function initScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -100px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                // Add stagger delay for feature cards
                if (entry.target.classList.contains('feature-card') ||
                    entry.target.classList.contains('comparison-card')) {
                    const cards = entry.target.parentElement.children;
                    const cardIndex = Array.from(cards).indexOf(entry.target);
                    entry.target.style.transitionDelay = `${cardIndex * 0.1}s`;
                }

                entry.target.classList.add('animate-in');

                // Trigger count-up animation for stat values
                if (entry.target.classList.contains('stat')) {
                    const valueElement = entry.target.querySelector('.stat-value.count-up');
                    if (valueElement && !valueElement.classList.contains('counted')) {
                        valueElement.classList.add('counted');
                        animateValue(valueElement, valueElement.dataset.target);
                    }
                }
            }
        });
    }, observerOptions);

    // Observe elements for scroll animations
    const elementsToAnimate = document.querySelectorAll(`
        .feature-card,
        .panel-window,
        .panel-feature,
        .comparison-card,
        .download-card,
        .stat,
        .gateway-text,
        .gateway-demo,
        .section-header
    `);

    elementsToAnimate.forEach(el => {
        observer.observe(el);
    });

    // Add animation styles
    addScrollAnimationStyles();
}

function addScrollAnimationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .feature-card,
        .panel-window,
        .panel-feature,
        .comparison-card,
        .download-card,
        .stat,
        .gateway-text,
        .gateway-demo,
        .section-header {
            opacity: 0;
            transform: translateY(30px);
            transition: opacity 0.6s ease, transform 0.6s ease;
        }

        .feature-card.animate-in,
        .panel-window.animate-in,
        .panel-feature.animate-in,
        .comparison-card.animate-in,
        .download-card.animate-in,
        .stat.animate-in,
        .gateway-text.animate-in,
        .gateway-demo.animate-in,
        .section-header.animate-in {
            opacity: 1;
            transform: translateY(0);
        }

        .gateway-text {
            transform: translateX(-30px);
        }

        .gateway-text.animate-in {
            transform: translateX(0);
        }

        .gateway-demo {
            transform: translateX(30px);
        }

        .gateway-demo.animate-in {
            transform: translateX(0);
        }
    `;
    document.head.appendChild(style);
}

// ============================================
// Fetch Live Stats from Gateway
// ============================================

async function fetchLiveStats() {
    const statsElement = document.getElementById('liveServers');
    if (!statsElement) return;

    try {
        const response = await fetch(GATEWAY_URL + '/api/stats', {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
            signal: AbortSignal.timeout(5000)
        });

        if (response.ok) {
            const data = await response.json();
            const serverCount = data.servers || 0;

            // Update the data-target attribute for count-up animation
            statsElement.dataset.target = serverCount;

            // If already visible, animate immediately
            if (statsElement.closest('.stat').classList.contains('animate-in')) {
                animateValue(statsElement, serverCount);
            }
        } else {
            statsElement.textContent = '--';
        }
    } catch (error) {
        console.log('[Stats] Gateway not available yet');
        statsElement.textContent = '--';
        statsElement.dataset.target = '0';
    }

    // Refresh every 30 seconds
    setTimeout(fetchLiveStats, 30000);
}

// ============================================
// Count-Up Animation
// ============================================

function initCountUpAnimations() {
    // Initialize count-up elements
    document.querySelectorAll('.count-up').forEach(el => {
        if (!el.dataset.target) {
            const currentText = el.textContent.trim();
            // Try to extract number from text like "10+" or "100%"
            const match = currentText.match(/\d+/);
            if (match) {
                el.dataset.target = match[0];
            } else {
                el.dataset.target = '0';
            }
        }
    });
}

function animateValue(element, target) {
    const targetValue = parseInt(target) || 0;
    const duration = 2000;
    const startValue = 0;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const eased = easeOutCubic(progress);
        const current = Math.round(startValue + (targetValue - startValue) * eased);

        element.textContent = current.toLocaleString();

        if (progress < 1) {
            requestAnimationFrame(update);
        } else {
            // Add suffix if original had one
            const originalText = element.textContent;
            if (element.classList.contains('has-suffix')) {
                element.textContent = current.toLocaleString() + '+';
            }
        }
    }

    requestAnimationFrame(update);
}

function easeOutCubic(t) {
    return 1 - Math.pow(1 - t, 3);
}

// ============================================
// Typing Animation for Gateway URL
// ============================================

function initTypingAnimation() {
    const typingElement = document.querySelector('.typing-animation');
    if (!typingElement) return;

    const text = typingElement.textContent;
    typingElement.textContent = '';
    typingElement.style.display = 'inline-block';
    typingElement.style.borderRight = '2px solid var(--primary-light)';
    typingElement.style.paddingRight = '2px';

    let index = 0;
    const typingSpeed = 150;

    function type() {
        if (index < text.length) {
            typingElement.textContent += text.charAt(index);
            index++;
            setTimeout(type, typingSpeed);
        } else {
            // Blinking cursor effect
            setInterval(() => {
                typingElement.style.borderRightColor =
                    typingElement.style.borderRightColor === 'transparent'
                        ? 'var(--primary-light)'
                        : 'transparent';
            }, 500);
        }
    }

    // Start typing when element comes into view
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && index === 0) {
                setTimeout(type, 500);
                observer.disconnect();
            }
        });
    }, { threshold: 0.5 });

    observer.observe(typingElement);
}

// ============================================
// Parallax Effect for Hero Background
// ============================================

window.addEventListener('scroll', () => {
    const scrolled = window.pageYOffset;
    const heroGlows = document.querySelectorAll('.hero-glow');

    heroGlows.forEach((glow, index) => {
        const speed = 0.3 + (index * 0.1);
        glow.style.transform = `translate(-50%, ${scrolled * speed}px)`;
    });
});

// ============================================
// Add Particle Effect to Hero
// ============================================

function createParticles() {
    const particlesContainer = document.querySelector('.floating-particles');
    if (!particlesContainer) return;

    const particleCount = 30;

    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.cssText = `
            position: absolute;
            width: ${Math.random() * 4 + 1}px;
            height: ${Math.random() * 4 + 1}px;
            background: hsla(215, 85%, 55%, ${Math.random() * 0.5 + 0.1});
            border-radius: 50%;
            left: ${Math.random() * 100}%;
            top: ${Math.random() * 100}%;
            animation: float ${Math.random() * 10 + 10}s ease-in-out infinite;
            animation-delay: ${Math.random() * 5}s;
            filter: blur(${Math.random() * 2}px);
        `;
        particlesContainer.appendChild(particle);
    }
}

// Initialize particles after a short delay
setTimeout(createParticles, 1000);

// ============================================
// Easter Egg: Konami Code
// ============================================

let konamiCode = [];
const konamiSequence = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'b', 'a'];

document.addEventListener('keydown', (e) => {
    konamiCode.push(e.key);
    konamiCode = konamiCode.slice(-konamiSequence.length);

    if (konamiCode.join('') === konamiSequence.join('')) {
        activateEasterEgg();
    }
});

function activateEasterEgg() {
    console.log('%c🎉 Konami Code Activated! 🎉', 'font-size: 24px; color: #2d7aed; font-weight: bold;');
    console.log('%cYou found the secret! Join our Discord for more surprises:', 'font-size: 16px; color: #8b5cf6;');
    console.log('%chttps://discord.gg/jQGMhKA5m6', 'font-size: 16px; color: #22c55e; font-weight: bold;');

    // Add rainbow animation to logo
    const logo = document.querySelector('.logo-icon');
    if (logo) {
        logo.style.animation = 'rainbow 2s linear infinite';

        const style = document.createElement('style');
        style.textContent = `
            @keyframes rainbow {
                0% { filter: hue-rotate(0deg); }
                100% { filter: hue-rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
    }
}

// ============================================
// Performance Monitoring
// ============================================

if ('PerformanceObserver' in window) {
    const perfObserver = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
            if (entry.entryType === 'largest-contentful-paint') {
                console.log(`LCP: ${entry.renderTime || entry.loadTime}ms`);
            }
        }
    });

    try {
        perfObserver.observe({ entryTypes: ['largest-contentful-paint'] });
    } catch (e) {
        // Browser doesn't support LCP
    }
}

// Log page load time
window.addEventListener('load', () => {
    setTimeout(() => {
        const perfData = performance.timing;
        const pageLoadTime = perfData.loadEventEnd - perfData.navigationStart;
        console.log(`%cPage loaded in ${pageLoadTime}ms`, 'color: #22c55e; font-weight: bold;');
    }, 0);
});

// ============================================
// Button Hover Effects with Bounce
// ============================================

function initButtonEffects() {
    const buttons = document.querySelectorAll('.btn');

    buttons.forEach(btn => {
        // Add magnetic effect on hover
        btn.addEventListener('mousemove', (e) => {
            const rect = btn.getBoundingClientRect();
            const x = e.clientX - rect.left - rect.width / 2;
            const y = e.clientY - rect.top - rect.height / 2;

            // Subtle magnetic pull effect
            btn.style.transform = `translate(${x * 0.1}px, ${y * 0.1}px) scale(1.05)`;
        });

        btn.addEventListener('mouseleave', () => {
            btn.style.transform = '';
        });

        // Add ripple effect on click
        btn.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();

            ripple.style.cssText = `
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.3);
                width: 100px;
                height: 100px;
                left: ${e.clientX - rect.left - 50}px;
                top: ${e.clientY - rect.top - 50}px;
                transform: scale(0);
                animation: rippleEffect 0.6s ease-out;
                pointer-events: none;
            `;

            this.appendChild(ripple);
            setTimeout(() => ripple.remove(), 600);
        });
    });

    // Add ripple animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes rippleEffect {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

// ============================================
// Panel Mock Animations
// ============================================

function initPanelMockAnimations() {
    const panelMock = document.querySelector('.panel-mock');
    if (!panelMock) return;

    // Animate stat values periodically
    const statValues = panelMock.querySelectorAll('.panel-stat-value');
    const originalValues = Array.from(statValues).map(el => parseInt(el.textContent) || 0);

    setInterval(() => {
        statValues.forEach((el, index) => {
            if (el.classList.contains('online')) {
                // Online players fluctuate
                const change = Math.floor(Math.random() * 5) - 2;
                const newValue = Math.max(15, Math.min(50, originalValues[index] + change));
                animateStatChange(el, parseInt(el.textContent), newValue);
                originalValues[index] = newValue;
            }
        });
    }, 5000);

    // Highlight nav items periodically
    const navItems = panelMock.querySelectorAll('.panel-nav-item');
    let currentActive = 0;

    setInterval(() => {
        navItems.forEach(item => item.classList.remove('active'));
        currentActive = (currentActive + 1) % navItems.length;
        navItems[currentActive].classList.add('active');

        // Update header title
        const headerTitle = panelMock.querySelector('.panel-header-title');
        if (headerTitle) {
            const titles = ['Dashboard', 'Players', 'Punishments', 'AutoMod', 'Activity', 'Settings'];
            headerTitle.textContent = titles[currentActive] || 'Dashboard';
        }
    }, 4000);

    // Add hover effects to table rows
    const tableRows = panelMock.querySelectorAll('.panel-table-row');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', () => {
            row.style.background = 'hsla(215, 85%, 55%, 0.1)';
        });
        row.addEventListener('mouseleave', () => {
            row.style.background = '';
        });
    });
}

function animateStatChange(element, from, to) {
    const duration = 500;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const current = Math.round(from + (to - from) * progress);
        element.textContent = current;

        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }

    requestAnimationFrame(update);
}

// ============================================
// Coming Soon Modal
// ============================================

function showComingSoon(e) {
    if (e) e.preventDefault();
    const btn = e?.currentTarget;

    // Button press animation first
    if (btn) {
        btn.style.transform = 'scale(0.95)';
    }

    // After brief animation, show popup with slide-in
    setTimeout(() => {
        if (btn) {
            btn.style.transform = '';
        }

        const modal = document.getElementById('comingSoonModal');
        if (modal) {
            modal.classList.add('show');
        }
    }, 150);
}

function hideComingSoon() {
    const modal = document.getElementById('comingSoonModal');
    if (modal) {
        modal.classList.remove('show');
    }
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        hideComingSoon();
        hideSignInModal();
    }
    // Close account dropdown when clicking outside
    const dropdown = document.getElementById('navAccountDropdown');
    const accountBtn = document.querySelector('.nav-account-btn');
    if (dropdown && accountBtn && !dropdown.contains(e.target) && !accountBtn.contains(e.target)) {
        dropdown.classList.remove('show');
    }
});

// Close modal on Escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        hideComingSoon();
        hideSignInModal();
    }
});

// ============================================
// Authentication System
// ============================================

function getSession() {
    try {
        const raw = localStorage.getItem(SESSION_KEY);
        if (!raw) return null;
        return JSON.parse(raw);
    } catch { return null; }
}

function saveSession(data) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(data));
}

function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

async function initAuth() {
    const session = getSession();
    if (session && session.token) {
        const valid = await validateSession(session.token);
        if (valid) {
            updateNavAuth(true);
        } else {
            clearSession();
            updateNavAuth(false);
        }
    } else {
        updateNavAuth(false);
    }
}

async function validateSession(token) {
    try {
        const res = await fetch(GATEWAY_URL + '/api/auth/session/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token })
        });
        if (!res.ok) return false;
        const data = await res.json();
        return data.valid === true;
    } catch { return false; }
}

async function loginWithPassword(username, password, cfTurnstileToken) {
    const body = { username, password };
    if (cfTurnstileToken) body.turnstileToken = cfTurnstileToken;
    const res = await fetch(GATEWAY_URL + '/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || 'Login failed');
    return data;
}

function updateNavAuth(loggedIn) {
    const signInBtn = document.getElementById('navSignInBtn');
    const accountEl = document.getElementById('navAccount');
    if (!signInBtn || !accountEl) return;

    if (loggedIn) {
        const session = getSession();
        signInBtn.style.display = 'none';
        accountEl.style.display = 'flex';
        const avatar = document.getElementById('navAvatar');
        const username = document.getElementById('navUsername');
        if (avatar && session) {
            avatar.src = `https://mc-heads.net/avatar/${session.uuid || session.username}/24`;
        }
        if (username && session) {
            username.textContent = session.username;
        }
    } else {
        signInBtn.style.display = '';
        accountEl.style.display = 'none';
    }
    updateReviewSubmitArea();
}

function toggleAccountDropdown() {
    const dropdown = document.getElementById('navAccountDropdown');
    if (dropdown) dropdown.classList.toggle('show');
}

function logout() {
    clearSession();
    updateNavAuth(false);
    const dropdown = document.getElementById('navAccountDropdown');
    if (dropdown) dropdown.classList.remove('show');
}

function showSignInModal() {
    const modal = document.getElementById('signInModal');
    if (modal) {
        modal.classList.add('show');
        const usernameInput = document.getElementById('signInUsername');
        if (usernameInput) setTimeout(() => usernameInput.focus(), 200);
        // Reset Turnstile widget on reopen
        resetTurnstile();
    }
}

function hideSignInModal() {
    const modal = document.getElementById('signInModal');
    if (modal) modal.classList.remove('show');
    const err = document.getElementById('signInError');
    if (err) { err.style.display = 'none'; err.textContent = ''; }
    resetTurnstile();
}

// ============================================
// Cloudflare Turnstile
// ============================================

function onTurnstileLoad() {
    const container = document.getElementById('turnstileWidget');
    if (!container || !window.turnstile) return;
    turnstileWidgetId = turnstile.render(container, {
        sitekey: TURNSTILE_SITE_KEY,
        theme: 'dark',
        callback: function(token) {
            turnstileToken = token;
            const btn = document.getElementById('signInSubmitBtn');
            if (btn) btn.disabled = false;
        },
        'expired-callback': function() {
            turnstileToken = null;
            const btn = document.getElementById('signInSubmitBtn');
            if (btn) btn.disabled = true;
        },
        'error-callback': function() {
            turnstileToken = null;
            const btn = document.getElementById('signInSubmitBtn');
            if (btn) btn.disabled = true;
        }
    });
}

function resetTurnstile() {
    turnstileToken = null;
    const btn = document.getElementById('signInSubmitBtn');
    if (btn) btn.disabled = true;
    if (turnstileWidgetId !== null && window.turnstile) {
        turnstile.reset(turnstileWidgetId);
    }
}

async function handleSignIn(e) {
    e.preventDefault();
    const username = document.getElementById('signInUsername').value.trim();
    const password = document.getElementById('signInPassword').value;
    const errorEl = document.getElementById('signInError');
    const submitBtn = document.getElementById('signInSubmitBtn');

    if (!username || !password) {
        errorEl.textContent = 'Please fill in all fields.';
        errorEl.style.display = 'block';
        return;
    }

    if (!turnstileToken) {
        errorEl.textContent = 'Please complete the verification.';
        errorEl.style.display = 'block';
        return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing in...';
    errorEl.style.display = 'none';

    try {
        const data = await loginWithPassword(username, password, turnstileToken);
        saveSession({
            token: data.token,
            username: data.username || username,
            uuid: data.uuid || ''
        });
        hideSignInModal();
        updateNavAuth(true);
        document.getElementById('signInPassword').value = '';
    } catch (err) {
        errorEl.textContent = err.message || 'Login failed. Please try again.';
        errorEl.style.display = 'block';
        resetTurnstile();
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-sign-in-alt"></i> Sign In';
    }
}

// ============================================
// Reviews System
// ============================================

async function initReviews() {
    await fetchReviews();
    updateReviewSubmitArea();
}

async function fetchReviews() {
    try {
        const res = await fetch(GATEWAY_URL + '/api/reviews', {
            signal: AbortSignal.timeout(8000)
        });
        if (res.ok) {
            const data = await res.json();
            allReviews = data.reviews || [];
        }
    } catch (err) {
        console.log('[Reviews] Could not fetch reviews:', err.message);
        allReviews = [];
    }
    renderReviews();
}

function renderReviews() {
    const grid = document.getElementById('reviewsGrid');
    const showMoreBtn = document.getElementById('reviewsShowMore');
    if (!grid) return;

    if (allReviews.length === 0) {
        grid.innerHTML = '<p style="text-align:center;color:var(--text-secondary);grid-column:1/-1;">No reviews yet. Be the first to share your experience!</p>';
        if (showMoreBtn) showMoreBtn.style.display = 'none';
        return;
    }

    const toShow = showingAllReviews ? allReviews : allReviews.slice(0, 6);
    grid.innerHTML = toShow.map(r => `
        <div class="review-card glass-card">
            <div class="review-header">
                <img class="review-avatar" src="https://mc-heads.net/avatar/${escapeHtml(r.minecraft_uuid || r.minecraft_username)}/32" alt="">
                <div class="review-info">
                    <span class="review-username">${escapeHtml(r.minecraft_username)}</span>
                    <span class="review-date">${formatReviewDate(r.created_at)}</span>
                </div>
            </div>
            <div class="review-stars">${renderStars(r.rating)}</div>
            <p class="review-description">${escapeHtml(r.description)}</p>
        </div>
    `).join('');

    if (showMoreBtn) {
        showMoreBtn.style.display = (!showingAllReviews && allReviews.length > 6) ? '' : 'none';
    }
}

function showAllReviews() {
    showingAllReviews = true;
    renderReviews();
}

function renderStars(rating) {
    let html = '';
    for (let i = 1; i <= 5; i++) {
        html += i <= rating
            ? '<i class="fas fa-star" style="color:#f59e0b;"></i>'
            : '<i class="far fa-star" style="color:#f59e0b;"></i>';
    }
    return html;
}

function selectRating(n) {
    selectedRating = n;
    for (let i = 1; i <= 5; i++) {
        const btn = document.getElementById('starBtn' + i);
        if (btn) {
            btn.innerHTML = i <= n
                ? '<i class="fas fa-star"></i>'
                : '<i class="far fa-star"></i>';
            btn.classList.toggle('active', i <= n);
        }
    }
}

function updateReviewSubmitArea() {
    const area = document.getElementById('reviewSubmitArea');
    if (!area) return;

    const session = getSession();
    if (!session || !session.token) {
        area.innerHTML = `
            <div style="text-align:center;">
                <button class="btn btn-primary" onclick="showSignInModal()">
                    <i class="fas fa-sign-in-alt"></i> Sign In to Leave a Review
                </button>
            </div>`;
        return;
    }

    const existing = allReviews.find(r =>
        r.minecraft_username && session.username &&
        r.minecraft_username.toLowerCase() === session.username.toLowerCase()
    );

    const prefillRating = existing ? existing.rating : 0;
    const prefillDesc = existing ? escapeHtml(existing.description) : '';
    const btnLabel = existing ? 'Update Your Review' : 'Submit Review';
    selectedRating = prefillRating;

    let starButtons = '';
    for (let i = 1; i <= 5; i++) {
        const filled = i <= prefillRating;
        starButtons += `<button type="button" class="review-star-btn ${filled ? 'active' : ''}" id="starBtn${i}" onclick="selectRating(${i})">
            <i class="${filled ? 'fas' : 'far'} fa-star"></i>
        </button>`;
    }

    area.innerHTML = `
        <div class="review-form glass-card">
            <h3>${existing ? 'Update Your Review' : 'Leave a Review'}</h3>
            <div class="review-star-select">${starButtons}</div>
            <textarea id="reviewDescription" class="modal-input" placeholder="Share your experience with ModereX... (10-500 characters)" maxlength="500" rows="3">${prefillDesc}</textarea>
            <div class="review-char-count"><span id="reviewCharCount">${prefillDesc.length}</span>/500</div>
            <button class="btn btn-primary" id="reviewSubmitBtn" onclick="submitReview()">
                <i class="fas fa-paper-plane"></i> ${btnLabel}
            </button>
        </div>`;

    const textarea = document.getElementById('reviewDescription');
    if (textarea) {
        textarea.addEventListener('input', () => {
            const count = document.getElementById('reviewCharCount');
            if (count) count.textContent = textarea.value.length;
        });
    }
}

async function submitReview() {
    const session = getSession();
    if (!session || !session.token) {
        showSignInModal();
        return;
    }

    const description = (document.getElementById('reviewDescription')?.value || '').trim();
    const btn = document.getElementById('reviewSubmitBtn');
    if (!btn) return;

    if (selectedRating < 1 || selectedRating > 5) {
        alert('Please select a star rating (1-5).');
        return;
    }
    if (description.length < 10 || description.length > 500) {
        alert('Description must be between 10 and 500 characters.');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...';

    try {
        const res = await fetch(GATEWAY_URL + '/api/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + session.token
            },
            body: JSON.stringify({ rating: selectedRating, description })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to submit review');

        await fetchReviews();
    } catch (err) {
        alert(err.message || 'Failed to submit review.');
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-paper-plane"></i> Submit Review';
    }
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function formatReviewDate(timestamp) {
    if (!timestamp) return '';
    const date = new Date(typeof timestamp === 'number' ? timestamp * 1000 : timestamp);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}
