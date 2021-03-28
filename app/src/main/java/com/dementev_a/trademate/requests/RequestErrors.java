package com.dementev_a.trademate.requests;

import com.dementev_a.trademate.R;

import java.util.Map;

public class RequestErrors {
    public static Map<String, Integer> errors = Map.of(
            // Sign Up Company Activity
            "Such company is already exist", R.string.sign_up_company_activity_company_with_this_name_exist_error_text,
            "Company with this email is already exist", R.string.sign_up_company_activity_company_with_this_email_exist_error_text,
            "Password is unreliable", R.string.sign_up_company_activity_password_is_unreliable_error_text,
            "Email is incorrect", R.string.sign_up_company_activity_incorrect_email_error_text,
            // Company Fragment
            "Such shop is already exist", R.string.company_fragment_shop_exist_error
    );

    public static Map<Integer, Integer> globalErrors = Map.of(
            RequestStatus.STATUS_SERVER_ERROR, R.string.global_errors_server_error_text,
            RequestStatus.STATUS_INTERNET_ERROR, R.string.global_errors_internet_connection_error_text,
            RequestStatus.STATUS_EMPTY_FIELDS, R.string.global_errors_empty_fields_error_text
    );
}
