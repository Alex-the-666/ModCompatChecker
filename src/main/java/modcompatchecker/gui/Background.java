package modcompatchecker.gui;

import modcompatchecker.Lang;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Background extends JPanel {
    private BufferedImage tileImage;

    public Background(boolean dark) {
        try {
            tileImage = ImageIO.read(getClass().getResource(dark ? "/darkmode.png" : "/lightmode.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if(tileImage != null){
            for (int x = 0; x < width; x += tileImage.getWidth()) {
                for (int y = 0; y < height; y += tileImage.getHeight()) {
                    g.drawImage(tileImage, x, y, this);
                }
            }
        }
    }
}
