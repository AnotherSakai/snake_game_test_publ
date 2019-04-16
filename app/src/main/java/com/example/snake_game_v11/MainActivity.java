package com.example.snake_game_v11;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    // Объявляем переменную SnakeView
    SnakeView snakeView;
    // Мы его инициализируем в onCreate
    // когда у нас будет больше информации о устройстве юзера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Находим высоту и шиирину
        Display display = getWindowManager().getDefaultDisplay();

        // Загружаем разрешение в Point
        Point size = new Point();
        display.getSize(size);

        // Создаем новый View Основанный на SnakeView class
        snakeView = new SnakeView(this, size);

        // Делаем snakeView основным "видом" Активности
        setContentView(snakeView);
    }

    // Начинаем поток в snakeView Когда эта Activity
    // показана Юзеру
    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }

    // Удостоверяемся,что snakeView остановилась
    // Если эта Activity будет закрыта
    @Override
    protected void onPause() {
        super.onPause();
        snakeView.pause();
    }
}