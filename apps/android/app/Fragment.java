package android.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Mock Fragment class for Frida agent testing
 */
public class Fragment {
    private Context context;

    public Fragment() {
        System.out.println("[Fragment] Fragment created");
    }

    public Context getContext() {
        System.out.println("[Fragment] getContext called");
        return context;
    }

    public void setContext(Context context) {
        System.out.println("[Fragment] setContext called");
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("[Fragment] onCreateView called");
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        System.out.println("[Fragment] onViewCreated called");
    }

    public void onCreate(Bundle savedInstanceState) {
        System.out.println("[Fragment] onCreate called");
    }

    public void onStart() {
        System.out.println("[Fragment] onStart called");
    }

    public void onResume() {
        System.out.println("[Fragment] onResume called");
    }

    public void onPause() {
        System.out.println("[Fragment] onPause called");
    }

    public void onStop() {
        System.out.println("[Fragment] onStop called");
    }

    public void onDestroy() {
        System.out.println("[Fragment] onDestroy called");
    }

    public void onDestroyView() {
        System.out.println("[Fragment] onDestroyView called");
    }
}
