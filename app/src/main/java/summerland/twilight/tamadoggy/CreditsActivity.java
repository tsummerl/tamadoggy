package summerland.twilight.tamadoggy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreditsActivity extends AppCompatActivity {

    Button buttonFreepik;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        buttonFreepik = findViewById(R.id.buttonFreePik);
        buttonFreepik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.freepik.com/free-photos-vectors/dog"));
                startActivity(i);
            }
        });
    }
}
