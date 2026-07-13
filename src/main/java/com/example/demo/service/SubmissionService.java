package com.example.demo.service;

import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.model.Submission;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class SubmissionService {

  private final SubmissionRepository submissionRepository;
  private final BucketComponent bucketComponent;
  private final ImageConverter imageConverter;
  private final Mailer mailer;

  public Submission submit(String email, MultipartFile image) throws Exception {
    var id = UUID.randomUUID();
    var originalFilename = image.getOriginalFilename();
    var extension = getExtension(originalFilename);
    var originalKey = "original/" + id + "." + extension;
    var processedKey = "processed/" + id + ".png";

    var tempFile = File.createTempFile("submission-", "." + extension);
    image.transferTo(tempFile);
    bucketComponent.upload(tempFile, originalKey);

    var imageBytes = Files.readAllBytes(tempFile.toPath());
    var grayscaleBytes = imageConverter.convertToGrayscale(imageBytes);

    var bwFile = File.createTempFile("submission-bw-", ".png");
    Files.write(bwFile.toPath(), grayscaleBytes);
    bucketComponent.upload(bwFile, processedKey);

    var submission = new Submission();
    submission.setId(id);
    submission.setEmail(email);
    submission.setFilename(originalFilename);
    submission.setCreatedAt(LocalDateTime.now());
    submissionRepository.save(submission);

    var presignedUrl = bucketComponent.presign(processedKey, Duration.ofHours(1));
    sendEmailWithImageLink(email, presignedUrl);

    return submission;
  }

  private void sendEmailWithImageLink(String email, URL presignedUrl) {
    try {
      var toAddress = new InternetAddress(email);
      var htmlBody =
          """
          <html>
          <body>
            <h2>Votre image convertie en noir et blanc est prête</h2>
            <p>Cliquez sur le lien ci-dessous pour télécharger votre image :</p>
            <a href="%s">Télécharger l'image</a>
            <p><i>Ce lien expire dans 1 heure.</i></p>
          </body>
          </html>
          """
              .formatted(presignedUrl);

      mailer.accept(
          new Email(
              toAddress,
              List.of(),
              List.of(),
              "Votre image noir et blanc est prête",
              htmlBody,
              List.of()));
    } catch (Exception e) {
      log.error("Failed to send email to {}", email, e);
    }
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "png";
    }
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }
}
