package com.iteqno.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.*
import com.iteqno.workmanager.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var workManager: WorkManager
    private lateinit var periodicTaskRequest: PeriodicWorkRequest
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(this)
        binding.btnOneTimeTask.setOnClickListener(this)
        binding.btnPeriodicTask.setOnClickListener(this)
        binding.btnCancelTask.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.btnOneTimeTask ->startOneTimeTask()
            R.id.btnPeriodicTask -> startPeriodicTask()
            R.id.btnCancelTask -> cancelPeriodicTask()
        }
    }

    private fun cancelPeriodicTask() {
        workManager.cancelWorkById(periodicTaskRequest.id)
    }

    private fun startPeriodicTask() {
        binding.textStatus.text = getString(R.string.status)
        val data  = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, binding.editCity.text.toString())
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicTaskRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(periodicTaskRequest)
        workManager.getWorkInfoByIdLiveData(periodicTaskRequest.id)
            .observe(this@MainActivity, { workInfo ->
                val status = workInfo.state.name
                binding.textStatus.append("\n" + status)
                binding.btnCancelTask.isEnabled = false
                if(workInfo.state == WorkInfo.State.ENQUEUED){
                    binding.btnCancelTask.isEnabled = true
                }

            })
    }

    private fun startOneTimeTask() {
        binding.textStatus.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, binding.editCity.text.toString())
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()
        workManager.enqueue(oneTimeWorkRequest)
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this@MainActivity,{ workInfo ->
                val status = workInfo.state.name
                binding.textStatus.append("\n" + status)
            })
    }
}