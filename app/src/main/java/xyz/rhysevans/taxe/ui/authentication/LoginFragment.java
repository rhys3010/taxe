package xyz.rhysevans.taxe.ui.authentication;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.rhysevans.taxe.R;

/**
 * LoginFragment.java
 * Fragment to display all the UI elements needed to login and send
 * credentials to API.
 * @author Rhys Evans
 * @version 0.1
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

}
