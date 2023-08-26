package com.example.chatboat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();

    private TextView txtResponse;
    private TextView idTVQuestion;
    private TextInputEditText etQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etQuestion = findViewById(R.id.etQuestion);
        idTVQuestion = findViewById(R.id.idTVQuestion);
        txtResponse = findViewById(R.id.txtResponse);

        etQuestion.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                txtResponse.setText("Please wait..");

                String question = etQuestion.getText().toString().trim();
                Toast.makeText(this, question, Toast.LENGTH_SHORT).show();
                if (!question.isEmpty()) {
                    getResponse(question, response -> runOnUiThread(() -> txtResponse.setText(response)));
                }else {
                    txtResponse.setText("Please ente valid query.");
                }
                return true;
            }
            return false;
        });
    }

    private void getResponse(String question, CallbackFunction callback) {
        idTVQuestion.setText(question);
        etQuestion.setText("");

        String apiKey = "API KEY";//"YOUR_API_KEY";


        String url = "https://api.openai.com/v1/engines/text-davinci-003/completions";

        String requestBody = "{\n" +
                "    \"prompt\": \"" + question + "\",\n" +
                "    \"max_tokens\": 500,\n" +
                "    \"temperature\": 0\n" +
                "}";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "API failed", e);
//                Toast.makeText(MainActivity.this, "API failed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    String textResult = jsonArray.getJSONObject(0).getString("text");
                    callback.call(textResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    interface CallbackFunction {
        void call(String response);
    }
}
