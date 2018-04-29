package idev.com.drawline;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import idev.com.drawline.Model.Path;
import idev.com.drawline.Model.Room;

public class InputPath extends DialogFragment {
    public CallbackResult callbackResult;
    private Spinner asal, tujuan;
    private String pilihanAsal, pilihanTujuan;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    private int request_code = 0;
    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.layout_input_path, container, false);

        asal = root_view.findViewById(R.id.spnAsal);
        tujuan = root_view.findViewById(R.id.spnTujuan);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final ArrayList<Room> roomList = bundle.getParcelableArrayList("data");
            String[] arraySpin = new String[roomList.size()];
            for (int i = 0; i < roomList.size(); i++) {
                arraySpin[i] = roomList.get(i).getRoom_name();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(InputPath.this.getContext(),
                    android.R.layout.simple_spinner_item, arraySpin);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(InputPath.this.getContext(),
                    android.R.layout.simple_spinner_item, arraySpin);
            asal.setAdapter(adapter);
            tujuan.setAdapter(adapter2);

            asal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    pilihanAsal = roomList.get(i).getRoom_code();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            tujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    pilihanTujuan = roomList.get(i).getRoom_code();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            root_view.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            root_view.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendDataResult(pilihanAsal, pilihanTujuan);
                    dismiss();
                }
            });
        }

        return root_view;
    }

    private void sendDataResult(String asal, String tujuan) {
        Path path = new Path(asal, tujuan);
        if (callbackResult != null) {
            callbackResult.sendResult(request_code, path);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    public interface CallbackResult {
        void sendResult(int requestCode, Object obj);
    }

    public String getPilihanAsal() {
        return pilihanAsal;
    }

    public String getPilihanTujuan() {
        return pilihanTujuan;
    }
}
