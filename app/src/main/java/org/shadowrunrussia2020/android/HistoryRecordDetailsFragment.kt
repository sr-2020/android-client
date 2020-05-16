package org.shadowrunrussia2020.android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_history_record_details.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import java.util.*

class HistoryRecordDetailsFragment : Fragment() {
    private val args: HistoryRecordDetailsFragmentArgs by navArgs()
    private val record: HistoryRecord by lazy { args.record }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_record_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textHistoryRecordTitle.text = record.title
        textHistoryRecordTime.text = PrettyTime(Locale("ru")).format(Date(record.timestamp))
        textHistoryRecordText.text =
            if (record.longText.isNotEmpty()) record.longText else record.shortText
    }
}
