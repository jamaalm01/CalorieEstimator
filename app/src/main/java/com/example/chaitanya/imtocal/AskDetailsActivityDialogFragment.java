package com.example.chaitanya.imtocal;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class AskDetailsActivityDialogFragment extends DialogFragment{


    public AskDetailsActivityDialogFragment() {
        // Required empty public constructor
    }

    private Spinner spinner_plateno;
    private Spinner spinner_platesize;
    private  Button btnDismiss;
    private  Button btnDone;
    private EditText editText_dispName;
    private static final int DATA_REQUEST_CODE = 1014;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final AskDetailsActivityFragment parentFragment = (AskDetailsActivityFragment)getTargetFragment();
        // Inflate the layout for this fragment

        //View view_askdetails = inflater.inflate(R.layout.fragment_sample_dialog, container, false);
        View view_askdetails = inflater.inflate(R.layout.fragment_ask_details_dialog, container, false);
        spinner_plateno = (Spinner) view_askdetails.findViewById(R.id.spinner_plateno);
        spinner_platesize = (Spinner) view_askdetails.findViewById(R.id.spinner_platesize);
        ArrayAdapter<CharSequence> adapter_plateno = ArrayAdapter.createFromResource(this.getActivity(),R.array.plates_no_array,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter_platesize = ArrayAdapter.createFromResource(this.getActivity(),R.array.plate_size_array,android.R.layout.simple_spinner_item);
        adapter_plateno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_platesize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_plateno.setAdapter(adapter_plateno);
        spinner_platesize.setAdapter(adapter_platesize);
        editText_dispName = (EditText) view_askdetails.findViewById(R.id.editText_projname);
        btnDismiss = (Button) view_askdetails.findViewById(R.id.button_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnDone = (Button) view_askdetails.findViewById(R.id.button_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsProject detailsProject = new DetailsProject();
                detailsProject.setDispName(editText_dispName.getText().toString().trim());
                detailsProject.setPlatesNo(Integer.parseInt(spinner_plateno.getSelectedItem().toString()));
                detailsProject.setPlateSize(spinner_platesize.getSelectedItem().toString());
                parentFragment.newDataReceived(detailsProject);
                dismiss();
            }
        });
        getDialog().setTitle("Input Details");
        return view_askdetails;
    }
    public interface DialogDataListener {
        public void newDataReceived(DetailsProject detailsProject);
    }

}
