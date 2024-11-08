package com.example.quokka_event.models.event;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;
import com.google.android.gms.common.util.Hex;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.UUID;

/**
 * Class for the organizer activities on in the QR Tab of an event
 */
public class QRFragment extends Fragment {
    ImageView qrImage;
    public QRFragment() {
        // leave empty
    }

    /**
     * Method for the creation if a QR Fragment
     * @author mylayambao
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this frag
        View view =  inflater.inflate(R.layout.event_view_qr_frag, container, false);

        Button generateQRCodeButton = view.findViewById(R.id.event_qr_generate_button);
        qrImage = view.findViewById(R.id.qr_image);

        // Set on click listener for the generate qr code button
        generateQRCodeButton.setOnClickListener(v->{
            try {
                generateQR();
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        });

        return view;
    }

    /**
     * Generates and displays a QR Code
     * @author mylayambao
     * @throws WriterException
     */

    private void generateQR() throws WriterException {
        // create the hash
//        String qrHash = UUID.randomUUID().toString();
        String qrHash = "2b86OkQwYUsrH2BCyRMm";
//        String qrHash = "This should say Bob";

        // create the qr map
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 800;
        int height = 800;

        // ref :https://stackoverflow.com/questions/41606384/how-to-generate-qr-code-using-zxing-library
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrHash, BarcodeFormat.QR_CODE, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // display the qr code
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }
}