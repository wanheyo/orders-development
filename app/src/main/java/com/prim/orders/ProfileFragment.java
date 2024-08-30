package com.prim.orders;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Users users = new Users();
    private Users usersEdit = new Users();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
        }

        //profile
        TableLayout tl1 = v.findViewById(R.id.tl_1);
        TextView tvName = v.findViewById(R.id.tvCustName);
        TextView tvPhone = v.findViewById(R.id.tvPhoneNum);
        TextView tvEmail = v.findViewById(R.id.tvEmail);
        tvName.setText(Html.fromHtml("<b>Name : </b>" + users.getName()));
        tvPhone.setText(Html.fromHtml("<b>Phone Num : </b>" + users.getTelno()));
        tvEmail.setText(Html.fromHtml("<b>Email : </b>" + users.getEmail()));

        TableLayout tl2 = v.findViewById(R.id.tl_2);
        EditText etName = v.findViewById(R.id.et_name);
        EditText etPhone = v.findViewById(R.id.et_phone);
        EditText etEmail = v.findViewById(R.id.et_email);
        etName.setText(users.getName());
        etPhone.setText(users.getTelno());
        etEmail.setText(users.getEmail());

        CardView cvBtnEdit = v.findViewById(R.id.cv_btn_edit);
        Button btnEdit = v.findViewById(R.id.btn_Edit);
        Button btnCancel = v.findViewById(R.id.btn_Cancel);

        tl2.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);




        etPhone.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etPhone.setTransformationMethod(new ProfileFragment.NumericKeyBoardTransformationMethod());
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // You can implement any specific behavior here if needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // You can implement any specific behavior here if needed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if the total length exceeds 13 characters, and trim if necessary
                if (editable.length() > 13) {
                    etPhone.setText(editable.subSequence(0, 13));
                    etPhone.setSelection(etPhone.getText().length());
                }

                // Check if the initial prefix is present, if not, re-add it
                if (!editable.toString().startsWith("+60")) {
                    etPhone.setText("+60");
                    etPhone.setSelection(etPhone.getText().length());
                }
            }
        });
        //etPhone.setFilters(new InputFilter[]{new PhoneNumberInputFilter()});
        //etPhone.setFormattedPhoneNumber(phoneNumberEditText, "+60192523415");

        cvBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tl1.setVisibility(View.GONE);
                tl2.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches() || etEmail.getText() == null) || (etName.getText() == null || etName.getText().length() >= 50) || (etPhone.getText() == null || etPhone.getText().length() < 12 || etPhone.getText().length() > 13)) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches() || etEmail.getText() == null) {
                        Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
                    } else if(etName.getText() == null || etName.getText().length() >= 50) {
                        Toast.makeText(getActivity(), "Invalid name, cannot exceed 50 chars", Toast.LENGTH_SHORT).show();
                    } else if(etPhone.getText() == null || etPhone.getText().length() < 12 || etPhone.getText().length() > 13) {
                        Toast.makeText(getActivity(), "Invalid phone number (Malaysia 60+)", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    users.setName(etName.getText().toString());
                    users.setEmail(etEmail.getText().toString());
                    users.setTelno(etPhone.getText().toString());
                    call_updateUser_API();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tl1.setVisibility(View.VISIBLE);
                tl2.setVisibility(View.GONE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

                etName.setText(users.getName());
                etPhone.setText(users.getTelno());
                etEmail.setText(users.getEmail());
            }
        });

        return v;
    }

    public void call_updateUser_API() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateUser(users.getId(), users.getName(), users.getTelno(), users.getEmail()).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateUser()", "Response: API Response, Success. " + response.body().getResponse());
                    Toast.makeText(getActivity(), "Updated successfully, login back", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateUser()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-updateUser()", "Response: API failed. " + t.getMessage());
                Toast.makeText(getActivity(), "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}