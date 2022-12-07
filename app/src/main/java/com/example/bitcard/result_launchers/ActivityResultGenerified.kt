package com.example.bitcard.result_launchers

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class ActivityResultGenerified<Input, Result>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    onActivityResult: OnActivityResult<Result>?
) {

    companion object {

        private fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>,
            onActivityResult: OnActivityResult<Result>?
        ): ActivityResultGenerified<Input, Result> {
            return ActivityResultGenerified(caller, contract, onActivityResult)
        }

        private fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>
        ): ActivityResultGenerified<Input, Result> {
            return registerForActivityResult(caller, contract, null)
        }

        /**
         * Specialised method for launching new activities.
         */
        fun registerActivityForResult(
            caller: ActivityResultCaller
        ): ActivityResultGenerified<Intent, ActivityResult> {
            return registerForActivityResult(caller,
                ActivityResultContracts.StartActivityForResult()
            )
        }

    }
    interface OnActivityResult<O> {
        /**
         * Called after receiving a result from the target activity
         */
        fun onActivityResult(result: O)
    }

    private var activityResultLauncher: ActivityResultLauncher<Input>
    private var onActivityResult: OnActivityResult<Result>

    init {
        this.onActivityResult = onActivityResult!!
        this.activityResultLauncher = caller.registerForActivityResult(
            contract
        ) { result: Result -> this.callOnActivityResult(result) }
    }

    private fun launch(input: Input, onActivityResult: OnActivityResult<Result>?) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult
        }
        activityResultLauncher.launch(input)
    }

    fun launch(input: Input) {
        launch(input, onActivityResult)
    }

    private fun callOnActivityResult(result: Result){
        onActivityResult.onActivityResult(result = result)
    }
}