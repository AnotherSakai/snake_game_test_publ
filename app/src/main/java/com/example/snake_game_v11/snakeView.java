package com.example.snake_game_v11;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

class SnakeView extends SurfaceView implements Runnable {

    private Thread m_Thread = null;
    private volatile boolean m_Playing;
    // То,на чем рисуем
    private Canvas m_Canvas;
    // Это будет отслыкой к Activity
    private Context m_context;
    // Для отслеживания движений m_Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Направление на старте
    private Direction m_Direction = Direction.RIGHT;

    // Контролируем паузы между обновлениями
    private long m_NextFrameTime;
    // Обновляем игру 10 раз в секунду
    private final long FPS = 10;
    private final long MILLIS_IN_A_SECOND = 1000;
    // Мы будем рисовать картинку намного чаще
    // Текущий m_Score
    private int m_Score;
    // Насколько длинная змейка в текущий момент
    private int m_SnakeLength;
    // Размер в сегментах играбельной зоны
    private final int NUM_BLOCKS_WIDE = 40;
    private int m_NumBlocksHigh; // определяется динамично

    // Это необходимо для Canvas и рисования
    private SurfaceHolder m_Holder;
    // Это позволяет контролировать нам цвета и т.д.
    private Paint m_Paint;
  //todo Все переменные будем выписывать ниже

    // Какое разрешение экрана
    private int m_ScreenWidth;
    private int m_ScreenHeight;

    // Размер пикселей змейки
    private int m_BlockSize;
    // Где яблоко
    private int m_AppleX;
    private int m_AppleY;


    //Местонахождение на поле из всех сегментов
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;




    //Дальше переменные будут ломать код
    public SnakeView(Context context, Point size) {
        super(context);

        m_context = context;
        // todo Ищем размер экрана
        //определяем размер
        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;
        //Определяем размер каждого блока/place на игровом поле
        m_BlockSize = m_ScreenWidth / NUM_BLOCKS_WIDE;
        // Сколько блоков одтнакового размера поместятся в высоту
        m_NumBlocksHigh = ((m_ScreenHeight)) / m_BlockSize;

        // максимальный размер змейки
        // Если вы набираете 200 вы вознаграждены достижением "крашнуть игру"
        m_SnakeXs = new int[200];
        m_SnakeYs = new int[200];

        m_Holder = getHolder();
        m_Paint = new Paint();

        // Начинаем игру
        startGame();
    }

    @Override
    public void run() {

        while (m_Playing) {

            // обновление 10 раз в секунду
            if(checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }
    //todo Создаем яблоко
    public void spawnApple() {
        Random random = new Random();
        m_AppleX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        m_AppleY = random.nextInt(m_NumBlocksHigh - 1) + 1;
    }
    public void startGame() {
        // Задаем длину змейки и её стартовую позицию
        m_SnakeLength = 15;
        m_SnakeXs[0] = NUM_BLOCKS_WIDE / 2;
        m_SnakeYs[0] = m_NumBlocksHigh / 2;

        //todo Вставляем метод появления яблока
        // И яблоко для съедения
        spawnApple();

        // обнуляем  m_Score
        m_Score = 0;
        m_NextFrameTime = System.currentTimeMillis();
    }


    private void eatApple(){

        // Увеличиваем размер змейки
        m_SnakeLength++;
        //переставляем яблоко
        spawnApple();
        //+1 в  m_Score
        m_Score = m_Score + 1;

    }

    private void moveSnake(){
        // проверка на возможность создать змейку
        for (int i = m_SnakeLength; i > 0; i--) {

            m_SnakeXs[i] = m_SnakeXs[i - 1];
            m_SnakeYs[i] = m_SnakeYs[i - 1];
        }
        //todo Метод поворота
        // Двигаем голову в подходящий m_Direction
        switch (m_Direction) {
            case UP:
                m_SnakeYs[0]--;
                break;

            case RIGHT:
                m_SnakeXs[0]++;
                break;

            case DOWN:
                m_SnakeYs[0]++;
                break;

            case LEFT:
                m_SnakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        // Жив/мертв
        boolean dead = false;

        // ударил стенку?
        if (m_SnakeXs[0] == -1) dead = true;
        if (m_SnakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (m_SnakeYs[0] == -1) dead = true;
        if (m_SnakeYs[0] == m_NumBlocksHigh) dead = true;

        // съел себя?
        for (int i = m_SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (m_SnakeXs[0] == m_SnakeXs[i]) && (m_SnakeYs[0] == m_SnakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void updateGame() {
        // коснулась ли голова яблока?
        if (m_SnakeXs[0] == m_AppleX && m_SnakeYs[0] == m_AppleY) {
            eatApple();
        }
        moveSnake();
        if (detectDeath()) {
            //по новой
            startGame();
        }
    }
    public void drawGame() {
        // Готовимся рисовать
        if (m_Holder.getSurface().isValid()) {
            m_Canvas = m_Holder.lockCanvas();
             //todo Балуемся цветами (потом)
            // Покрываем поле цветом
            m_Canvas.drawColor(Color.argb(255, 120, 197, 87));

            // Выбираем цвет змейки и яблока (и счета)
            m_Paint.setColor(Color.argb(255, 255, 255, 255));

            // Размер счета
            m_Paint.setTextSize(30);
            m_Canvas.drawText("Score:" + m_Score, 10, 30, m_Paint);


            //Рисуем змейку
            for (int i = 0; i < m_SnakeLength; i++) {
                m_Canvas.drawRect(m_SnakeXs[i] * m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize),
                        (m_SnakeXs[i] * m_BlockSize) + m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize) + m_BlockSize,
                        m_Paint);
            }

            //рисуем яблоко
            m_Canvas.drawRect(m_AppleX * m_BlockSize,
                    (m_AppleY * m_BlockSize),
                    (m_AppleX * m_BlockSize) + m_BlockSize,
                    (m_AppleY * m_BlockSize) + m_BlockSize,
                    m_Paint);

            // рисуем весь кадр
            m_Holder.unlockCanvasAndPost(m_Canvas);
        }

    }

    public boolean checkForUpdate() {
        if(m_NextFrameTime <= System.currentTimeMillis()){
            // Десятая секунды прошла

            // триггер на обновление
            m_NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;

            // Возвращаем true что приводит к обновлению и
            // исполнению рисующих функций
            return true;
        }

        return false;
    }


    public void pause() {
        m_Playing = false;
        try {
            m_Thread.join();
        } catch (InterruptedException e) {
            // Ошибка
        }
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();
    }
   //todo Пооврот змейки
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= m_ScreenWidth / 2) {
                    switch(m_Direction){
                        case UP:
                            m_Direction = Direction.RIGHT;//вправо
                            break;
                        case RIGHT:
                            m_Direction = Direction.DOWN;//вниз
                            break;
                        case DOWN:
                            m_Direction = Direction.LEFT;//влево
                            break;
                        case LEFT:
                            m_Direction = Direction.UP;//вверх
                            break;
                    }
                }

                   else {
                    //todo Поворот против часовой
                    switch(m_Direction){
                        case UP:
                            m_Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            m_Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            m_Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            m_Direction = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }
}
