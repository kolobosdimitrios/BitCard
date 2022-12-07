package com.example.bitcard.result_launchers

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class PermissionResultGenerified {

    class PermissionResultGenerified<Permission, Decision> private constructor(
        caller: ActivityResultCaller,
        contract: ActivityResultContract<Permission, Decision>,
        private var onPermissionResult: OnPermissionResult<Decision>?
    ) {
        private val permissionLauncher: ActivityResultLauncher<Permission>

        interface OnPermissionResult<O> {
            fun onPermissionResult(result: O)
        }

        init {
            permissionLauncher = caller.registerForActivityResult(
                contract
            ) { result: Decision -> callOnActivityResult(result) }
        }

        fun setOnPermissionResult(onPermissionResult: OnPermissionResult<Decision>?) {
            this.onPermissionResult = onPermissionResult
        }

        private fun callOnActivityResult(result: Decision) {
            if (onPermissionResult != null) onPermissionResult!!.onPermissionResult(result)
        }

        @JvmOverloads
        fun ask(
            permission: Permission,
            onPermissionResult: OnPermissionResult<Decision>? = this.onPermissionResult
        ) {
            if (onPermissionResult != null) {
                this.onPermissionResult = onPermissionResult
            }
            permissionLauncher.launch(permission)
        }

        companion object {
            private fun <Permission, Decision> registerForPermissionResult(
                caller: ActivityResultCaller,
                contract: ActivityResultContract<Permission, Decision>,
                onPermissionResult: OnPermissionResult<Decision>?
            ): PermissionResultGenerified<Permission, Decision> {
                return PermissionResultGenerified(caller, contract, onPermissionResult)
            }

            private fun <Permission, Decision> registerForPermissionResult(
                caller: ActivityResultCaller,
                contract: ActivityResultContract<Permission, Decision>
            ): PermissionResultGenerified<Permission, Decision> {
                return registerForPermissionResult(caller, contract, null)
            }

            fun registerForPermissionResult(
                caller: ActivityResultCaller
            ): PermissionResultGenerified<String, Boolean> {
                return registerForPermissionResult(caller,
                    ActivityResultContracts.RequestPermission()
                )
            }
        }
    }

}