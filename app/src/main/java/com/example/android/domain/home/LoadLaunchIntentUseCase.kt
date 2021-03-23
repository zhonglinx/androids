package com.example.android.domain.home

import android.content.Context
import android.content.Intent
import com.example.android.di.IoDispatcher
import com.example.android.domain.FlowUseCase
import com.example.android.model.LaunchIntent
import com.example.android.model.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadLaunchIntentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<LaunchIntent>>(ioDispatcher) {

    override suspend fun execute(parameter: Unit): Flow<Result<List<LaunchIntent>>> {
        return flow {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_SAMPLE_CODE)
            }
            val pm = context.packageManager

            val launchIntent = pm.queryIntentActivities(intent, 0).map { info ->
                val label = info.loadLabel(pm)?.toString() ?: info.activityInfo.name
                LaunchIntent(
                    label,
                    activityIntent(info.activityInfo.packageName, info.activityInfo.name)
                )
            }
            emit(Result.Success(launchIntent))
        }
    }

    private fun activityIntent(pkg: String, componentName: String): Intent {
        return Intent().apply { setClassName(pkg, componentName) }
    }
}