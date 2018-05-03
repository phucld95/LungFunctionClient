package vn.hust.soict.lung_function.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.hust.soict.lung_function.R;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.utils.FontUtils;

/**
 * Created by tulc on 17/03/2017.
 */
public class PatientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClickItemListener {
        void onClick(int position);
    }

    private OnClickItemListener mListener;
    private List<Profile> mList;
    private Context mContext;

    public PatientAdapter(Context context, List<Profile> list, OnClickItemListener onClickItemListener) {
        mContext = context;
        mList = list;
        mListener = onClickItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        PatientViewHolder viewHolder;
        View root = layoutInflater.inflate(R.layout.item_add_patient, parent, false);

        viewHolder = new PatientViewHolder(root);
        viewHolder.iconPatient.setImageResource(R.drawable.ic_person_white);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            viewHolder.iconPatient.setColorFilter(mContext.getColor(R.color.tintIcon));
        } else {
            viewHolder.iconPatient.setColorFilter(mContext.getResources().getColor(R.color.tintIcon));
        }
        viewHolder.iconPatient.setBackgroundResource(R.drawable.bg_icon_patient);
        FontUtils.setFont(viewHolder.textPatient);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PatientViewHolder) {
            PatientViewHolder viewHolder = (PatientViewHolder) holder;
            viewHolder.textPatient.setText(mList.get(position).getName());
            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public ImageView iconPatient;
        public TextView textPatient;

        public PatientViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            iconPatient = (ImageView) itemView.findViewById(R.id.iconPatient);
            textPatient = (TextView) itemView.findViewById(R.id.textPatient);
        }
    }
}
