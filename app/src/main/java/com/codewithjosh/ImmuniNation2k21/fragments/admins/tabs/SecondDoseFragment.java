package com.codewithjosh.ImmuniNation2k21.fragments.admins.tabs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.adapters.RequestAdapter;
import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SecondDoseFragment extends Fragment {

    RecyclerView recyclerRequests;
    int requestStatus;
    Context context;
    FirebaseFirestore firebaseFirestore;
    private RequestAdapter requestAdapter;
    private List<RequestModel> requests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_schedule_request, container, false);

        initViews(view);
        initInstances();
        loadRequests();

        return view;

    }

    private void initViews(final View view) {

        if (getContext() != null) context = getContext();

        recyclerRequests = view.findViewById(R.id.recycler_requests);

        initRecyclerView();

        requestStatus = 2;

        requests = new ArrayList<>();
        requestAdapter = new RequestAdapter(context, requests, requestStatus);
        recyclerRequests.setAdapter(requestAdapter);

    }

    private void initRecyclerView() {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerRequests.setLayoutManager(linearLayoutManager);
        recyclerRequests.setHasFixedSize(true);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void loadRequests() {

        firebaseFirestore
                .collection("Requests")
                .whereEqualTo("request_status", requestStatus)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null) {

                        if (isConnected()) onLoadRequests(value);

                        else
                            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();

                    }

                });

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void onLoadRequests(final QuerySnapshot value) {

        requests.clear();
        for (QueryDocumentSnapshot snapshot : value) {

            final RequestModel request = snapshot.toObject(RequestModel.class);
            requests.add(request);
            requestAdapter.notifyDataSetChanged();

        }

    }
}