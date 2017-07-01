package com.example.rohitsingla.scrapman.amazonaws.mobilehelper.auth;
//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.16
//

import android.app.Activity;

/**
 * Interface for handling results from calling IdentityManager's signInOrSignUp().
 */
public interface SignInResultHandler {
    /**
     * Called when the user has obtained an identity by signing in with a provider.
     *
     * @param callingActivity the calling activity that should be finished.
     * @param provider the provider or null if succeeded with an unauthenticated identity.
     */
    void onSuccess(Activity callingActivity, IdentityProvider provider);

    /**
     * User cancelled signing in with a provider on the sign-in activity.
     * Note: The user is still on the sign-in activity when this call is made.
     * @param provider the provider the user canceled with.
     */
    void onIntermediateProviderCancel(Activity callingActivity, IdentityProvider provider);

    /**
     * User encountered an error when attempting to sign-in with a provider.
     * Note: The user is still on the sign-in activity when this call is made.
     * @param provider the provider the user attempted to sign-in with that encountered an error.
     * @param ex the exception that occurred.
     */
    void onIntermediateProviderError(Activity callingActivity, IdentityProvider provider, Exception ex);

    /**
     * User pressed back from the sign-in Activity.
     *
     * @return true if the activity should be finished, otherwise false.
     */
    boolean onCancel(Activity callingActivity);
}