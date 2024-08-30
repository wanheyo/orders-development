package com.prim.orders;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.MediaHandler;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopProfileFragment extends Fragment {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Users users = new Users();
    private Users usersEdit = new Users();
    private Organizations organizations = new Organizations();

    ImageView iv_edit_org_image;
    File fileImage;
    MultipartBody.Part filePart;
    MediaHandler mediaHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_profile, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
        }

        //shop profile
        TableLayout tlShop1 = v.findViewById(R.id.tl_shop_1);
        ImageView iv_org_image = v.findViewById(R.id.iv_org_image);
        TextView tvShopName = v.findViewById(R.id.tv_Shop_Name);
        TextView tvFullAddress = v.findViewById(R.id.tv_Full_Address);
        if(this != null) {
            Glide.with(this)
                    .load(baseURL + "/organization-picture/" + organizations.getOrganization_picture())
                    .placeholder(R.drawable.no_picture_icon)
                    .into(iv_org_image);
        }
        tvShopName.setText(Html.fromHtml("<b>Shop Name : </b>" + organizations.getNama()));
        tvFullAddress.setText(Html.fromHtml("<b>Full Address : </b>" + organizations.getAddress() + ", " + organizations.getCity() + ", " + organizations.getPostcode() + " " + organizations.getDistrict() + ", " + organizations.getState()));

        TableLayout tlShop2 = v.findViewById(R.id.tl_shop_2);
        iv_edit_org_image = v.findViewById(R.id.iv_edit_org_image);
        EditText etShopName = v.findViewById(R.id.et_shop_name);
        EditText etAddress = v.findViewById(R.id.et_address);
        EditText etCity = v.findViewById(R.id.et_city);
        EditText etPostCode = v.findViewById(R.id.et_postcode);
        EditText etDistrict = v.findViewById(R.id.et_district);
        EditText etState = v.findViewById(R.id.et_state);
        if(this != null) {
            Glide.with(this)
                    .load(baseURL + "/organization-picture/" + organizations.getOrganization_picture())
                    .placeholder(R.drawable.no_picture_icon)
                    .into(iv_edit_org_image);
        }
        etShopName.setText(organizations.getNama());
        etAddress.setText(organizations.getAddress());
        etCity.setText(organizations.getCity());
        etPostCode.setText(organizations.getPostcode());
        etDistrict.setText(organizations.getDistrict());
        etState.setText(organizations.getState());

        CardView cvBtnShopEdit = v.findViewById(R.id.cv_btn_shop_edit);
        Button btnShopEdit = v.findViewById(R.id.btn_Shop_Edit);
        Button btnShopCancel = v.findViewById(R.id.btn_Shop_Cancel);

        tlShop2.setVisibility(View.GONE);
        btnShopEdit.setVisibility(View.GONE);
        btnShopCancel.setVisibility(View.GONE);

        etPostCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etPostCode.setTransformationMethod(new ShopProfileFragment.NumericKeyBoardTransformationMethod());

        cvBtnShopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tlShop1.setVisibility(View.GONE);
                tlShop2.setVisibility(View.VISIBLE);
                btnShopEdit.setVisibility(View.VISIBLE);
                btnShopCancel.setVisibility(View.VISIBLE);

                if(this != null) {
                    Glide.with(ShopProfileFragment.this)
                            .load(baseURL + "/organization-picture/" + organizations.getOrganization_picture())
                            .placeholder(R.drawable.no_picture_icon)
                            .into(iv_org_image);
                }
            }
        });

        iv_edit_org_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

        btnShopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidText(etShopName.getText()) || !isValidText(etAddress.getText()) || !isValidText(etCity.getText()) ||
                        !isValidPostCode(String.valueOf(etPostCode.getText())) || !isValidText(etDistrict.getText()) || !isValidText(etState.getText())) {
                    // Display Toast messages for each specific error
                    if (!isValidText(etShopName.getText())) {
                        Toast.makeText(getActivity(), "Invalid shop name", Toast.LENGTH_SHORT).show();
                    } else if (!isValidText(etAddress.getText())) {
                        Toast.makeText(getActivity(), "Invalid address", Toast.LENGTH_SHORT).show();
                    } else if (!isValidText(etCity.getText())) {
                        Toast.makeText(getActivity(), "Invalid city", Toast.LENGTH_SHORT).show();
                    } else if (!isValidPostCode(String.valueOf(etPostCode.getText()))) {
                        Toast.makeText(getActivity(), "Invalid post code", Toast.LENGTH_SHORT).show();
                    } else if (!isValidText(etDistrict.getText())) {
                        Toast.makeText(getActivity(), "Invalid district", Toast.LENGTH_SHORT).show();
                    } else if (!isValidText(etState.getText())) {
                        Toast.makeText(getActivity(), "Invalid state", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    organizations.setNama(etShopName.getText().toString());
                    organizations.setAddress(etAddress.getText().toString());
                    organizations.setCity(etCity.getText().toString());
                    organizations.setPostcode(etPostCode.getText().toString());
                    organizations.setDistrict(etDistrict.getText().toString());
                    organizations.setState(etState.getText().toString());
                    call_updateOrganization_API();
                    // All inputs are valid, proceed with your logic
                }
            }
        });

        btnShopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tlShop1.setVisibility(View.VISIBLE);
                tlShop2.setVisibility(View.GONE);
                btnShopEdit.setVisibility(View.GONE);
                btnShopCancel.setVisibility(View.GONE);

                etShopName.setText(organizations.getNama());
                etAddress.setText(organizations.getAddress());
                etCity.setText(organizations.getCity());
                etPostCode.setText(organizations.getPostcode());
                etDistrict.setText(organizations.getDistrict());
                etState.setText(organizations.getState());
            }
        });

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
        etPhone.setTransformationMethod(new ShopProfileFragment.NumericKeyBoardTransformationMethod());
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

    private boolean isValidText(CharSequence text) {
        return text != null && !text.toString().trim().isEmpty();
    }

    private boolean isValidPostCode(String postCode) {
        return isValidText(postCode) && (postCode.length() == 5);
    }

    public void call_updateOrganization_API() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MultipartBody.Part filePart = null;
        if(fileImage != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
            filePart = MultipartBody.Part.createFormData("organization_pic", fileImage.getName(), requestBody);
        }
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("dish_image", fileImage.getName(), requestBody);
        //RequestBody orgIdBody = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(organizations.getId()));
        RequestBody organIdBody = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(organizations.getId()));
        RequestBody organNamaBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getNama());
        RequestBody organAddressBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getAddress());
        RequestBody organCityBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getCity());
        RequestBody organDistrictBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getDistrict());
        RequestBody organPostCodeBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getPostcode());
        RequestBody organStateBody = RequestBody.create(MediaType.parse("text/plain"), organizations.getState());

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateOrganization(organIdBody, organNamaBody, organAddressBody, organCityBody, organDistrictBody, organPostCodeBody, organStateBody, filePart).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateOrganization()", "Response: API Response, Success. " + response.body().getResponse());
                    Toast.makeText(getActivity(), "Updated successfully, login back", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateOrganization()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-updateOrganization()", "Response: API failed. " + t.getMessage());
                Toast.makeText(getActivity(), "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String path = mediaHandler.getPath(getActivity(), selectedImage);
            fileImage = new File(path);
            Log.d("onActivityResult()", "Response: Image Path. " + path);
            //ivAddDish.setImageURI(selectedImage);
            Glide.with(getActivity())
                    //.load(baseURL + "/organization-picture/" + organizationsList.get(position).getOrganization_picture())
                    .load(selectedImage)
                    .placeholder(R.drawable.no_picture_icon)
                    .into(iv_edit_org_image);
        }
    }

    class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}