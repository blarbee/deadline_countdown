import static android.os.Build.VERSION_CODES.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.order_confirmation))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {} )
                .create();
    }

}
