package com.prim.orders.utilities;

import android.database.Observable;

import com.prim.orders.models.Dish_Available;
import com.prim.orders.models.Dish_Type;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organization_User;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.models.api_respond.API_Resp_getOrderAvailableDish;
import com.prim.orders.models.api_respond.API_Resp_getOrderCart;
import com.prim.orders.models.api_respond.API_Resp_getReport;
import com.prim.orders.models.api_respond.API_Resp_listDishesByShop;
import com.prim.orders.models.api_respond.API_Resp_listOADAdmin;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @GET("/api/OrderS/test")
    Call<ArrayList<Users>> testData();

    @FormUrlEncoded
    @POST("/api/OrderS/login")
    Call<Users> login(@Field("email") String email, @Field("password") String password, @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("/api/OrderS/isUserOrderSAdmin")
    Call<Organization_User> isUserOrderSAdmin(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("/api/OrderS/logout")
    Call<Users> logout(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("/api/OrderS/updateUser")
    Call<API_Resp> updateUser(@Field("user_id") int user_id, @Field("name") String name, @Field("telno") String telno, @Field("email") String email);

    @Multipart
    @POST("/api/OrderS/updateOrganization")
    Call<API_Resp> updateOrganization(@Part("organ_id") RequestBody organ_id,
                                      @Part("nama") RequestBody nama,
                                      @Part("address") RequestBody address,
                                      @Part("city") RequestBody city,
                                      @Part("district") RequestBody district,
                                      @Part("postcode") RequestBody postcode,
                                      @Part("state") RequestBody state,
                                      @Part MultipartBody.Part organization_pic);

    @GET("/api/OrderS/randomDishes")
    Call<ArrayList<Dishes>> randomDishes();

    @GET("/api/OrderS/listDishes")
    Call<ArrayList<Dishes>> listDishes();

    @GET("/api/OrderS/listShops")
    Call<ArrayList<Organizations>> listShops();

    @FormUrlEncoded
    @POST("/api/OrderS/listDishesByShop")
    Call<ArrayList<Dishes>> listDishesByShop(@Field("organ_id") int org_id);

    @FormUrlEncoded
    @POST("/api/OrderS/listDishesByShopAdmin")
    Call<ArrayList<Dishes>> listDishesByShopAdmin(@Field("organ_id") int org_id);

    @FormUrlEncoded
    @POST("/api/OrderS/listDishAvailable")
    Call<ArrayList<Dish_Available>> listDishAvailable(@Field("dish_id") int dish_id);

    @FormUrlEncoded
    @POST("/api/OrderS/listOrderAvailable")
//    Call<ArrayList<Order_Available>> listOrderAvailable(@Field("dish_id") int dish_id);
    Call<ArrayList<Order_Available>> listOrderAvailable(@Field("organ_id") int organ_id);

    @FormUrlEncoded
    @POST("/api/OrderS/getOrderCart")
    Call<API_Resp_getOrderCart> getOrderCart(@Field("user_id") int user_id, @Field("organ_id") int organ_id);

    @FormUrlEncoded
    @POST("/api/OrderS/createOrderCart")
    Call<API_Resp> createOrderCart(@Field("quantity") int quantity, @Field("totalprice") double totalprice, @Field("order_available_id") int order_available_id, @Field("order_cart_id") int order_cart_id, @Field("total_amount") Double total_amount);

    @FormUrlEncoded
    @POST("/api/OrderS/getOrderAvailableDish")
    Call<API_Resp_getOrderAvailableDish> getOrderAvailableDish(@Field("user_id") int user_id, @Field("option") int option);

    @GET("/api/OrderS/getDishType")
    Call<ArrayList<Dish_Type>> getDishType();

//    @FormUrlEncoded
//    @POST("/api/OrderS/addDishes")
//    Call<Dishes> addDishes(@Field("organ_id") int organ_id, @Field("dish_name") String name, @Field("dish_price") double price, @Field("dish_type") int dish_type, @Field("dish_image") String dish_image);

    @Multipart
    @POST("/api/OrderS/addDishes")
    Call<API_Resp> addDishes(@Part("organ_id") RequestBody organ_id,
                             @Part("dish_name") RequestBody name,
                             @Part("dish_price") RequestBody price,
                             @Part("dish_type") RequestBody dish_type,
                             @Part MultipartBody.Part dish_image);

    @Multipart
    @POST("/api/OrderS/updateDishes")
    Call<API_Resp> updateDishes(@Part("dish_id") RequestBody dish_id,
                                @Part("organ_id") RequestBody organ_id,
                                @Part("dish_name") RequestBody name,
                                @Part("dish_price") RequestBody price,
                                @Part("dish_type") RequestBody dish_type,
                                @Part MultipartBody.Part dish_image);

    @FormUrlEncoded
    @POST("/api/OrderS/deleteDishes")
    Call<API_Resp> deleteDishes(@Field("dish_id") int dish_id);

    @FormUrlEncoded
    @POST("/api/OrderS/addOrderAvailable")
    Call<API_Resp> addOrderAvailable(@Field("dish_id") int dish_id, @Field("open_date") Date open_date, @Field("close_date") Date close_date, @Field("delivery_date") Date delivery_date, @Field("delivery_address") String delivery_address, @Field("quantity") int quantity);

    @FormUrlEncoded
    @POST("/api/OrderS/updateOrderAvailable")
    Call<API_Resp> updateOrderAvailable(@Field("dish_id") int dish_id, @Field("order_available_id") int order_available_id, @Field("open_date") Date open_date, @Field("close_date") Date close_date, @Field("delivery_date") Date delivery_date, @Field("delivery_address") String delivery_address, @Field("quantity") int quantity);

    @FormUrlEncoded
    @POST("/api/OrderS/deleteOrderAvailable")
    Call<API_Resp> deleteOrderAvailable(@Field("order_available_id") int order_available_id);

    @FormUrlEncoded
    @POST("/api/OrderS/listOrderAvailableAdmin")
    Call<ArrayList<Order_Available>> listOrderAvailableAdmin(@Field("dish_id") int dish_id);

    @FormUrlEncoded
    @POST("/api/OrderS/listOADAdmin")
    Call<API_Resp_listOADAdmin> listOADAdmin(@Field("order_available_id") int order_available_id);

    @FormUrlEncoded
    @POST("/api/OrderS/updateOADAdmin")
    Call<API_Resp> updateOADAdmin(@Field("order_available_dish_id") int order_available_dish_id, @Field("order_cart_id") int order_cart_id, @Field("delivery_status") String delivery_status);

    @FormUrlEncoded
    @POST("/api/OrderS/getUsers")
    Call<Users> getUsers(@Field("organ_id") int organ_id);

    @FormUrlEncoded
    @POST("/api/OrderS/getReport")
    Call<ArrayList<API_Resp_getReport>> getReport(@Field("organ_id") int organ_id, @Field("date") Date date, @Field("start_date") Date start_date, @Field("end_date") Date end_date);
    //Call<Users> fetchData(@Query("parameterName") String parameterValue);
    // Define other API endpoints as needed
}

