package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepListActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {

    private StepDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
//  TODO clean up      FloatingActionButton nextStep = findViewById(R.id.next_step);
//        nextStep.setOnClickListener(view -> changeStep(1));
//        FloatingActionButton previousStep = findViewById(R.id.previous_step);
//        previousStep.setOnClickListener(view -> changeStep(-1));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            startFragment();
        }
    }

//    private void changeStep(int i) {
//        if (fragment != null)
//            fragment.refresh(i);
//    }

    private void startFragment() {
        Bundle arguments = new Bundle();
        int stepId = getIntent().getIntExtra(StepDetailFragment.STEPS_ID, -1);
        arguments.putInt(StepDetailFragment.STEPS_ID, stepId);
        fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container,
                fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = new Intent(this, StepListActivity.class);
            if (fragment != null) {
                upIntent.putExtra(getString(R.string.recipe), fragment.recipe);
            }
            NavUtils.navigateUpTo(this, upIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}