package com.finance.financeProject.serviceImpl;

import com.finance.financeProject.service.QRService;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.util.Utils;
import org.springframework.stereotype.Service;

@Service
public class QRServiceImpl implements QRService {

    @Override
    public String generateQRCode(String email, String secret) throws QrGenerationException {

        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("ForgeHub")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator qrGenerator = new ZxingPngQrGenerator(); //PNG image

        byte[] image = qrGenerator.generate(data);   //byte[] contains the actual binary image data

        return Utils.getDataUriForImage(
                image,
                qrGenerator.getImageMimeType() //     MIME type = image/png, image = PNG bytes


        );
    }
}