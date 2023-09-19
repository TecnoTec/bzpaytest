package com.rnavarro.bzpaylogin.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rnavarro.bzpaylogin.Create_pet;
import com.rnavarro.bzpaylogin.R;
import com.rnavarro.bzpaylogin.model.Pet;

import java.text.DecimalFormat;

public class PetAdapter extends FirestoreRecyclerAdapter<Pet, PetAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PetAdapter(@NonNull FirestoreRecyclerOptions<Pet> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Pet Pet) {
        DecimalFormat format = new DecimalFormat("0.00");
//      format.setMaximumFractionDigits(2);
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(Pet.getName());
        viewHolder.age.setText(Pet.getAge());
        viewHolder.color.setText(Pet.getColor());
        viewHolder.vaccine_price.setText( format.format(Pet.getVaccine_price()));
        String photoPet = Pet.getPhoto();

        viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//          SEND DATA ACTIVITY
                Intent i = new Intent(activity, Create_pet.class);
                i.putExtra("id_pet", id);
                activity.startActivity(i);

//          SEND DATA FRAGMENT
//            CreatePetFragment createPetFragment = new CreatePetFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("id_pet", id);
//            createPetFragment.setArguments(bundle);
//            createPetFragment.show(fm, "open fragment");
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePet(id);
            }
        });
    }

    private void deletePet(String id) {
        mFirestore.collection("pet").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, color, vaccine_price;
        ImageView btn_delete, btn_edit, photo_pet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.Edt_nombre);
            age = itemView.findViewById(R.id.Edt_date);
            color = itemView.findViewById(R.id.Edt_plantel);
            vaccine_price = itemView.findViewById(R.id.Edt_grado);
            photo_pet = itemView.findViewById(R.id.photo);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);
        }
    }
}
