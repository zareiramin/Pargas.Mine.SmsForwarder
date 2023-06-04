package ir.pargasit.smsforwarder.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.pargasit.smsforwarder.MyApplication;
import ir.pargasit.smsforwarder.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ArrayList<String> allSms = new ArrayList<>();
    private MyRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(receiver, new IntentFilter("from_my_sms_listener"));

        String sourceNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("source_number", "");
        Boolean show_sms_form_all_numbers = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_sms_form_all_numbers", false);

        if (show_sms_form_all_numbers)
            binding.lblMessagesFrom.setText("همه پیامک‌های دریافتی:");
        else
            binding.lblMessagesFrom.setText("پیامک های دریافتی از شماره " + sourceNumber + ":");

        String ip = getIPAddress(true);
        binding.lblYourIp.setText("آی پی دستگاه شما: " + ip);

        // recycler view setting
        adapter = new MyRecyclerViewAdapter(getContext(), allSms);
        binding.rvAllSms.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAllSms.setAdapter(adapter);

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSms.clear();
                adapter.notifyDataSetChanged();
            }
        });


        View view = binding.getRoot();
        return view;


    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String str = intent.getStringExtra("msg");
                allSms.add(str);
                adapter.notifyItemInserted(allSms.size() - 1);
            }
        }
    };


    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

}