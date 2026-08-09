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
        
        // Ép WebView hiển thị cấu hình tương đương màn hình máy tính để không bị bóp menu
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectTvEnhancements(view);
            }
        });

        webView.loadUrl("https://hhpanda.st/");
    }

    // TỐI ƯU GIAO DIỆN VÀ HỆ THỐNG ĐIỀU HƯỚNG CHO TV
    private void injectTvEnhancements(WebView view) {
        String script = "javascript:(function() {" +
            "if (window.hasTvEnhanced) return;" +
            "window.hasTvEnhanced = true;" +
            "" +
            // 1. Ép các menu ẩn của mobile hiện ra trên màn hình TV để có thể điều hướng tới
            "const style = document.createElement('style');" +
            "style.innerHTML = '" +
            "  .navbar-collapse.collapse { display: block !important; height: auto !important; visibility: visible !important; }" +
            "  .navbar-toggle { display: none !important; }" +
            "  body { overflow-x: hidden; }" +
            "';" +
            "document.head.appendChild(style);" +
            "" +
            "let currentIndex = 0;" +
            "let focusableElements = [];" +
            "" +
            "function updateElements() {" +
            "    // Quét toàn bộ các thẻ tương tác được trên trang (menu, poster, nút bấm, lịch chiếu)" +
            "    const raw = document.querySelectorAll('a, button, input');" +
            "    focusableElements = Array.from(raw).filter(el => {" +
            "        const rect = el.getBoundingClientRect();" +
            "        // Chỉ lấy các phần tử thực sự hiển thị trên màn hình" +
            "        return rect.width > 0 && rect.height > 0 && window.getComputedStyle(el).visibility !== 'hidden';" +
            "    });" +
            "    if (focusableElements.length > 0 && currentIndex >= focusableElements.length) {" +
            "        currentIndex = 0;" +
            "    }" +
            "}" +
            "" +
            "function renderFocus() {" +
            "    updateElements();" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) {" +
            "            el.style.outline = '4px solid #00e5ff';" +
            "            el.style.transform = 'scale(1.05)';" +
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
            "setTimeout(renderFocus, 1000);" +
            "" +
            // 2. Thuật toán điều hướng không gian (Spatial Navigation) dựa trên tọa độ thực tế
            "function moveFocus(dir) {" +
            "    updateElements();" +
            "    if (focusableElements.length === 0) return;" +
            "" +
            "    const currentEl = focusableElements[currentIndex] || focusableElements[0];" +
            "    const curRect = currentEl.getBoundingClientRect();" +
            "    let bestIndex = currentIndex;" +
            "    let minScore = Infinity;" +
            "" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) return;" +
            "        const rect = el.getBoundingClientRect();" +
            "        const dx = rect.left + rect.width/2 - (curRect.left + curRect.width/2);" +
            "        const dy = rect.top + rect.height/2 - (curRect.top + curRect.height/2);" +
            "" +
            "        let isValid = false;" +
            "        let score = Infinity;" +
            "" +
            "        if (dir === 'right' && dx > 0) {" +
            "            score = Math.abs(dy) * 1.5 + dx;" +
            "            isValid = true;" +
            "        } else if (dir === 'left' && dx < 0) {" +
            "            score = Math.abs(dy) * 1.5 + Math.abs(dx);" +
            "            isValid = true;" +
            "ouncements" +
            "        } else if (dir === 'down' && dy > 0) {" +
            "            score = Math.abs(dx) * 1.5 + dy;" +
            "            isValid = true;" +
            "        } else if (dir === 'up' && dy < 0) {" +
            "            score = Math.abs(dx) * 1.5 + Math.abs(dy);" +
            "            isValid = true;" +
            "        }" +
            "" +
            "        if (isValid && score < minScore) {" +
            "            minScore = score;" +
            "            bestIndex = idx;" +
            "        }" +
            "    });" +
            "" +
            "    if (bestIndex !== currentIndex) {" +
            "        currentIndex = bestIndex;" +
            "        renderFocus();" +
            "    }" +
            "}" +
            "" +
            "document.addEventListener('keydown', function(e) {" +
            "    if (e.key === 'ArrowRight') { e.preventDefault(); moveFocus('right'); }" +
            "    else if (e.key === 'ArrowLeft') { e.preventDefault(); moveFocus('left'); }" +
            "    else if (e.key === 'ArrowDown') { e.preventDefault(); moveFocus('down'); }" +
            "    else if (e.key === 'ArrowUp') { e.preventDefault(); moveFocus('up'); }" +
            "    else if (e.key === 'Enter' || e.keyCode === 13 || e.keyCode === 23) {" +
            "        e.preventDefault();" +
            "        if (focusableElements[currentIndex]) {" +
            "            focusableElements[currentIndex].click();" +
            "            setTimeout(renderFocus, 2000);" +
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
