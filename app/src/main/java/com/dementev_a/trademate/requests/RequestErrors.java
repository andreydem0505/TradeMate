package com.dementev_a.trademate.requests;

import com.dementev_a.trademate.R;

import java.util.HashMap;
import java.util.Map;

public class RequestErrors {
    public static Map<String, Integer> errors = new HashMap<>();

    static {
        // Global
        errors.put("Password is unreliable", R.string.global_errors_password_is_unreliable_error_text);
        errors.put("Email is incorrect", R.string.global_errors_email_is_incorrect_error_text);
        // Sign Up Company Activity
        errors.put("Such company is already exist", R.string.sign_up_company_activity_company_with_this_name_exist_error_text);
        errors.put("Company with this email is already exist", R.string.sign_up_company_activity_company_with_this_email_exist_error_text);
        // Company Fragment
        errors.put("Such shop is already exist", R.string.company_fragment_shop_exist_error);
        // Log In Company Activity
        errors.put( "Company with this email wasn't found", R.string.log_in_company_activity_company_was_not_found_error_text);
        errors.put("Password is incorrect", R.string.log_in_company_activity_wrong_password_error_text);
        // Add Merchandiser Activity
        errors.put("Merchandiser with this name is already exist", R.string.add_merchandiser_activity_merchandiser_exist_with_name_error_text);
        errors.put("Merchandiser with this email is already exist", R.string.add_merchandiser_activity_merchandiser_exist_with_email_error_text);
        // Add Operator Activity
        errors.put("Operator with this name is already exist", R.string.add_operator_activity_operator_with_name_exist_error_text);
        errors.put("Operator with this email is already exist", R.string.add_operator_activity_operator_with_email_exist_error_text);
    }

    public static Map<Integer, Integer> globalErrors = Map.of(
            RequestStatus.STATUS_SERVER_ERROR, R.string.global_errors_server_error_text,
            RequestStatus.STATUS_INTERNET_ERROR, R.string.global_errors_internet_connection_error_text,
            RequestStatus.STATUS_EMPTY_FIELDS, R.string.global_errors_empty_fields_error_text
    );
}
