package com.example.rfif_android;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class giaodien1 extends AppCompatActivity {
    private ImageView iconHome, iconCard, iconSetting;
    private ImageView currentIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giaodien1);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragment()).commit();
        }

        iconHome = findViewById(R.id.icon_home);
        iconCard = findViewById(R.id.icon_card);
        iconSetting = findViewById(R.id.icon_setting);

        iconHome.setOnClickListener(v -> onIconClick(iconHome, new homeFragment()));
        iconCard.setOnClickListener(v -> onIconClick(iconCard, new cardFragment()));
        iconSetting.setOnClickListener(v -> onIconClick(iconSetting, new settingFragment()));

        onIconClick(iconHome, new homeFragment());
    }

    private void onIconClick(ImageView icon, androidx.fragment.app.Fragment fragment) {
        if (icon == currentIcon) return;
        resetAllIcons();
//        animateIcon(icon);
        changeIconColor(icon, Color.BLACK);
        currentIcon = icon;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void animateIcon(ImageView icon) {
        // Phóng to icon (scale từ 1.0 lên 1.3)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 1.0f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 1.0f, 1.3f);

        scaleX.setDuration(300); // 300ms
        scaleY.setDuration(300);

        scaleX.start();
        scaleY.start();
    }

    private void resetAllIcons() {
        // Đưa các icon về kích thước mặc định (1.0) và đổi lại màu trắng
        resetIcon(iconHome);
        resetIcon(iconCard);
        resetIcon(iconSetting);
    }

    private void resetIcon(ImageView icon) {
        icon.setScaleX(1.0f);
        icon.setScaleY(1.0f);
        changeIconColor(icon, Color.LTGRAY); // Đổi lại màu trắng
    }
    private void changeIconColor(ImageView icon, int color) {
        icon.setColorFilter(color);
    }

}