package com.example.demo.service;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class ImageConverter {

  @SneakyThrows
  public byte[] convertToGrayscale(byte[] imageBytes) {
    var inputStream = new ByteArrayInputStream(imageBytes);
    var original = ImageIO.read(inputStream);
    var grayscale =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(original, grayscale);

    var outputStream = new ByteArrayOutputStream();
    ImageIO.write(grayscale, "png", outputStream);
    return outputStream.toByteArray();
  }
}
