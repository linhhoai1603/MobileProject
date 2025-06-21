package com.mobile.fe_bankproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mobile.fe_bankproject.adapter.TransactionAdapter;
import com.mobile.fe_bankproject.dto.TransactionResponse;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.network.TransactionService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        String acct = getIntent().getStringExtra("account_number");

        rv = findViewById(R.id.rvTransactions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        rv.setAdapter(adapter);

        TransactionService svc = RetrofitClient.getInstance().getTransactionService();
        svc.getHistory(acct, 0, 50)
           .enqueue(new Callback<List<TransactionResponse>>() {
               @Override
               public void onResponse(Call<List<TransactionResponse>> call,
                                      Response<List<TransactionResponse>> response) {
                   if (response.isSuccessful() && response.body() != null) {
                       adapter.submitList(response.body());
                   }
               }

               @Override
               public void onFailure(Call<List<TransactionResponse>> call, Throwable t) {
                   // TODO: show error message
               }
           });
    }
}