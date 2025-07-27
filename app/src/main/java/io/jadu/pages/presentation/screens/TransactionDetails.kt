package io.jadu.pages.presentation.screens

import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import io.jadu.pages.ui.theme.PagesTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TransactionDetails(
    val serviceName: String,
    val paymentMethod: String,
    val status: String,
    val time: LocalTime,
    val date: LocalDate,
    val transactionId: String,
    val price: Double,
    val fee: Double,
    val total: Double
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillPaymentSuccessScreen(
    transactionDetails: TransactionDetails,
    onBackPressed: () -> Unit = {},
    onMoreOptionsPressed: () -> Unit = {},
    onShareReceiptPressed: () -> Unit = {},
    onCopyTransactionId: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bill Payment",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMoreOptionsPressed) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = Color(0xFF4A9B8E),
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4A9B8E))
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A9B8E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Payment Successfully",
                        color = Color(0xFF4A9B8E),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = transactionDetails.serviceName,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    TransactionDetailsSection(
                        transactionDetails = transactionDetails,
                        onCopyTransactionId = onCopyTransactionId
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onShareReceiptPressed,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4A9B8E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Share Receipt",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TransactionDetailsSection(
    transactionDetails: TransactionDetails,
    onCopyTransactionId: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Transaction details",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TransactionDetailRow(
            label = "Payment method",
            value = transactionDetails.paymentMethod
        )

        TransactionDetailRow(
            label = "Status",
            value = transactionDetails.status,
            valueColor = Color(0xFF4A9B8E)
        )

        TransactionDetailRow(
            label = "Time",
            value = transactionDetails.time.format(DateTimeFormatter.ofPattern("HH:mm a")).uppercase()
        )

        TransactionDetailRow(
            label = "Date",
            value = transactionDetails.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        )

        TransactionDetailRow(
            label = "Transaction ID",
            value = transactionDetails.transactionId,
            showCopyIcon = true,
            onCopyClick = onCopyTransactionId
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionDetailRow(
            label = "Price",
            value = "$${String.format("%.2f", transactionDetails.price)}"
        )

        TransactionDetailRow(
            label = "Fee",
            value = "- $${String.format("%.2f", transactionDetails.fee)}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        TransactionDetailRow(
            label = "Total",
            value = "$${String.format("%.2f", transactionDetails.total)}",
            isTotal = true
        )
    }
}

@Composable
private fun TransactionDetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colors.primary,
    showCopyIcon: Boolean = false,
    onCopyClick: () -> Unit = {},
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = valueColor,
                fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
                fontSize = 16.sp
            )

            if (showCopyIcon) {
                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun BillPaymentSuccessScreenPreview() {
    PagesTheme {
        BillPaymentSuccessScreen(
            transactionDetails = TransactionDetails(
                serviceName = "Youtube Premium",
                paymentMethod = "Debit Card",
                status = "Completed",
                time = LocalTime.of(8, 15),
                date = LocalDate.of(2022, 2, 28),
                transactionId = "20929138324725...",
                price = 11.99,
                fee = 1.99,
                total = 13.98
            )
        )
    }
}