package app.amplify.emptyapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtilityOptions
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import java.lang.Exception

// For some reason, it is used like this in code snippet
private val AWSCredentialsProvider.s3UploadPath: String
    get() {
        TODO("Not yet implemented")
    }

class MainActivity : AppCompatActivity() {

    // These values are not provided in snippet and hence just using the placeholders
    private lateinit var mediaFile: File
    private lateinit var runAttemptCount: Any
    private lateinit var transferRecordId: Any

    private val region: Region? = Region.getRegion("us-west-2")
    private lateinit var s3CredentialsProvider: AWSCredentialsProvider
    private lateinit var clientConfiguration: ClientConfiguration
    private lateinit var experiments: IdeapincreationExperiments
    // End of placeholders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        callCodeSnippet()
    }

    private fun callCodeSnippet() {
        val s3Client = AmazonS3Client(
            s3CredentialsProvider,
            region,
            clientConfiguration
        )

        val transferUtility = createTransferUtility(
            context = applicationContext,
            s3Client = s3Client,
            bucketName = "DUMMY BUCKET",
            experiments = experiments
        )

        val uploadObserver = if (runAttemptCount == 0 || transferRecordId == -1) {
            transferUtility.upload(s3CredentialsProvider.s3UploadPath, mediaFile)
        } else {
            transferUtility.resume(transferRecordId as Int)
        }

        val listener = object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                TODO("Not yet implemented")
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                TODO("Not yet implemented")
            }

            override fun onError(id: Int, ex: Exception?) {
                TODO("Not yet implemented")
            }

        }
        uploadObserver.setTransferListener(listener)
//        val transferState = Single.create<TransferState> { emitter ->
//            uploadObserver?.setTransferListener(
//                UploadListener(
//                    <all_parameters>
//            )
//            )
//        }.blockingGet()
//        if (transferState == COMPLETED) {
//        //handle success case
//        }
    }

    private fun createTransferUtility(
        context: Context,
        s3Client: AmazonS3Client,
        bucketName: String?,
        experiments: IdeapincreationExperiments
    ): TransferUtility {
        if (bucketName.isNullOrEmpty()) {
            throw IllegalArgumentException("Invalid Bucket name, it should not be null or empty")
        }
        val tuOptions = TransferUtilityOptions()
        if (experiments.isIdeaPinConcurrentChunkUploadEnabledAndActivate) {
        // We have to add capping logic to decide max number of threads. For now, 4 threads to
        //            get some data.
            tuOptions.transferThreadPoolSize = 4
        } else {
            tuOptions.transferThreadPoolSize = SINGLE
        }
        return TransferUtility.builder()
            .s3Client(s3Client)
            .context(context)
            .transferUtilityOptions(tuOptions)
            .defaultBucket(bucketName)
            .build()
    }

    companion object {
        private const val SINGLE: Int = 1
    }
}

// Dummy class to make the code snippet work
class IdeapincreationExperiments {

    val isIdeaPinConcurrentChunkUploadEnabledAndActivate: Boolean = false
}
