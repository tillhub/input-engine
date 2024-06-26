package de.tillhub.inputengine.ui.percentage

import AppTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.BundleCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.formatter.PercentageFormatter
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.percentage.PercentageInputData.Companion.EMPTY
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier

class PercentageInputActivity : ComponentActivity() {

    private val viewModel by viewModels<PercentageInputViewModel>()

    private val request: PercentageInputRequest by lazy {
        intent.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRA_REQUEST, PercentageInputRequest::class.java)
        } ?: throw IllegalArgumentException("Argument PercentageInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(request)

        val title = when (val stringParam = request.toolbarTitle) {
            is StringParam.String -> stringParam.value
            is StringParam.StringResource -> getString(stringParam.resIdRes)
        }

        setContent {
            val displayData by viewModel.percentageInput.collectAsStateWithLifecycle()
            PercentageScreen(title, displayData)
        }
    }

    @Preview
    @Composable
    internal fun PercentageScreen(
        title: String = "",
        data: PercentageInputData = EMPTY
    ) {
        AppTheme {
            Scaffold(
                modifier = getModifierBasedOnDeviceType(
                    isTablet = TabletScaffoldModifier,
                    isMobile = Modifier
                ),
                topBar = {
                    Toolbar(title = title) {
                        finish()
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(top = 16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    InputPreview(
                        percentText = data.text,
                        percentageMin = request.percentageMin,
                        percentageMax = request.percentageMax
                    )
                    Numpad(
                        onClick = viewModel::input,
                        showDecimalSeparator = request.allowDecimal
                    )
                    SubmitButton(data.isValid) {
                        val resultIntent = Intent().apply {
                            putExtra(ExtraKeys.EXTRAS_RESULT, data.percent)
                            putExtra(ExtraKeys.EXTRAS_ARGS, request.extras)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun InputPreview(
        percentText: String = "",
        percentageMin: PercentageParam = PercentageParam.Disable,
        percentageMax: PercentageParam = PercentageParam.Disable
    ) {
        if (percentageMax is PercentageParam.Enable) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                text = stringResource(
                    R.string.max_value, PercentageFormatter.format(percentageMax.percent)
                ),
                color = MagneticGrey,
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .testTag("percentValue"),
            style = MaterialTheme.typography.displaySmall,
            maxLines = 1,
            text = percentText,
            color = OrbitalBlue,
        )
        if (percentageMin is PercentageParam.Enable) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                text = stringResource(
                    R.string.min_value, PercentageFormatter.format(percentageMin.percent)
                ),
                color = MagneticGrey,
            )
        }
    }
}