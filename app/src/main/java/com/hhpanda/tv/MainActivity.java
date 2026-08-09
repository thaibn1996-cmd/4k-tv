package com.hhpanda.tv;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectTvFocusEngine(view);
            }
        });

        webView.loadUrl("https://hhpanda.st/");
    }

    private void injectTvFocusEngine(WebView view) {
        String script = "javascript:(function() {" +
            "if (window.hasTvFocusEngine) return;" +
            "window.hasTvFocusEngine = true;" +
            "" +
            "let currentIndex = 0;" +
            "let focusableElements = [];" +
            "" +
            "function updateFocusables() {" +
            "    // Lọc lấy các thẻ link, poster phim, nút bấm thực sự hiển thị trên trang" +
            "    const raw = document.querySelectorAll('a.halim-trending-link, a.halim-thumb, .page-numbers, header a, .site-header a');" +
            "    const allClickables = raw.length > 0 ? raw : document.querySelectorAll('a, button');" +
            "    focusableElements = Array.from(allClickables).filter(el => {" +
            "        const rect = el.getBoundingClientRect();" +
            "        return rect.width > 0 && rect.height > 0 && window.getComputedStyle(el).visibility !== 'hidden';" +
            "    });" +
            "    if (focusableElements.length > 0 && currentIndex >= focusableElements.length) {" +
            "        currentIndex = 0;" +
            "    }" +
            "}" +
            "" +
            "function applyFocus() {" +
            "    updateFocusables();" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) {" +
            "            el.style.outline = '4px solid #00e5ff';" +
            "            el.style.transform = 'scale(1.06)';" +
            "            el.style.boxShadow = '0 0 25px rgba(0, 229, 255, 0.9)';" +
            "            el.style.zIndex = '999999';" +
            "            el.style.transition = 'all 0.15s ease';" +
            "            el.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'center' });" +
            "        } else {" +
            "            el.style.outline = '';" +
            "            el.style.transform = '';" +
            "            el.style.boxShadow = '';" +
            "            el.style.zIndex = '';" +
            "        }" +
            "    });" +
            "}" +
            "" +
            "setTimeout(applyFocus, 1000);" +
            "" +
            "// Thuật toán tìm phần tử gần nhất theo hướng mũi tên (Spatial Navigation)" +
            "function navigate(direction) {" +
            "    updateFocusables();" +
            "    if (focusableElements.length === 0) return;" +
            "    let currentRect = focusableElements[currentIndex].getBoundingClientRect();" +
            "    let bestIndex = currentIndex;" +
            "    let minDist = Infinity;" +
            "" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) return;" +
            "        let rect = el.getBoundingClientRect();" +
            "        let dx = rect.left - currentRect.left;" +
            "        let dy = rect.top - currentRect.top;" +
            "        let dist = Math.sqrt(dx * dx + dy * dy);" +
            "" +
            "        if (direction === 'down' && dy > 0 && Math.abs(dx) < 350) {" +
            "            if (dist < minDist) { minDist = dist; bestIndex = idx; }" +
            "        } else if (direction === 'up' && dy < 0 && Math.abs(dx) < 350) {" +
            "            if (dist < minDist) { minDist = dist; bestIndex = idx; }" +
            "        } else if (direction === 'right' && dx > 0 && Math.abs(dy) < 150) {" +
            "            if (dist < minDist) { minDist = dist; bestIndex = idx; }" +
            "        } else if (direction === 'left' && dx < 0 && Math.abs(dy) < 150) {" +
            "            if (dist < minDist) { minDist = dist; bestIndex = idx; }" +
            "        }" +
            "    });" +
            "" +
            "    if (bestIndex !== currentIndex) {" +
            "        currentIndex = bestIndex;" +
            "        applyFocus();" +
            "    }" +
            "}" +
            "" +
            "document.addEventListener('keydown', function(e) {" +
            "    if (e.key === 'ArrowRight') { e.preventDefault(); navigate('right'); }" +
            "    else if (e.key === 'ArrowLeft') { e.preventDefault(); navigate('left'); }" +
            "    else if (e.key === 'ArrowDown') { e.preventDefault(); navigate('down'); }" +
            "    else if (e.key === 'ArrowUp') { e.preventDefault(); navigate('up'); }" +
            "    else if (e.key === 'Enter' || e.keyCode === 13 || e.keyCode === 23) {" +
            "        e.preventDefault();" +
            "        if (focusableElements[currentIndex]) {" +
            "            focusableElements[currentIndex].click();" +
            "            setTimeout(applyFocus, 1800);" +
            "        }" +
            "    }" +
            "}, true);" +
            "})();";
        view.evaluateJavascript(script, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
