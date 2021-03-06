/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 developers-payu-latam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.payu.sdk.payments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.payu.sdk.PayU;
import com.payu.sdk.PayUPayments;
import com.payu.sdk.exceptions.ConnectionException;
import com.payu.sdk.exceptions.SDKException;
import com.payu.sdk.model.Currency;
import com.payu.sdk.model.Language;
import com.payu.sdk.model.PaymentCountry;
import com.payu.sdk.model.PaymentMethod;
import com.payu.sdk.model.TransactionResponse;
import com.payu.sdk.util.TestEnvironment;
import com.payu.sdk.utils.LoggerUtil;

/**
 *
 * @author PayULatam
 * @since 1.0.0
 * @date 30/09/2013
 * @version 1.0.0
 */
public class CashMexicoApiIntegrationTest {

	/**
	 * Default response log message
	 */
	private static final String RESPONSE_LOG_MESSAGE = "{0}";

	@BeforeClass
	private void init() {
		PayU.apiKey = "012345678901";
		PayU.apiLogin = "012345678901";
		PayU.merchantId = "1";
		PayU.language = Language.en;
		PayU.isTest = false;

		TestEnvironment enviroment = TestEnvironment.LOCAL;

		PayU.paymentsUrl = enviroment.getPaymentsApiUrl();

		LoggerUtil.setLogLevel(Level.ALL);
	}

	/**
	 * Do authorization and capture
	 */
	@Test(enabled = true)
	public void testPaymentMethods() {

		PaymentMethod[] paymentMethods = { /*PaymentMethod.BAJIO,

		PaymentMethod.BANAMEX,

		PaymentMethod.HSBC,*/

		PaymentMethod.BANCOMER,

		PaymentMethod.IXE,

		PaymentMethod.SANTANDER,

		PaymentMethod.SCOTIABANK };

		for (PaymentMethod paymentMethod : paymentMethods) {
			doAuthorizationAndCapture(paymentMethod);
		}

	}

	/**
	 * Do authorization and capture
	 *
	 * @param paymentMethod
	 */
	public void doAuthorizationAndCapture(PaymentMethod paymentMethod) {

		Thread.currentThread().setName(
				"doAuthorizationAndCapture" + paymentMethod.name());

		Map<String, String> parameters = new HashMap<String, String>();

		Integer accountId = 11;
		parameters.put(PayU.PARAMETERS.ACCOUNT_ID, accountId.toString());

		String orderReferenceCode = "A1B2C3";
		parameters.put(PayU.PARAMETERS.REFERENCE_CODE, orderReferenceCode);

		String description = "ALL IN 5";
		parameters.put(PayU.PARAMETERS.DESCRIPTION, description);

		// Values
		Currency txCurrency = Currency.MXN;
		parameters.put(PayU.PARAMETERS.CURRENCY, txCurrency.toString());

		BigDecimal txValue = new BigDecimal(100).setScale(2,
				RoundingMode.HALF_UP);
		parameters.put(PayU.PARAMETERS.VALUE, txValue.toString());

		String nameOnCard = "NAME " + Long.toString(System.currentTimeMillis());
		parameters.put(PayU.PARAMETERS.PAYER_NAME, nameOnCard);

		parameters.put(PayU.PARAMETERS.PAYER_DNI, "8006479");

		parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, paymentMethod.name());

		parameters.put(PayU.PARAMETERS.COUNTRY, PaymentCountry.MX.name());

		TransactionResponse paymentResponse = null;
		try {
			paymentResponse = PayUPayments
					.doAuthorizationAndCapture(parameters);

			LoggerUtil.info(RESPONSE_LOG_MESSAGE, paymentResponse);

			paymentResponse.getTransactionId();

			Assert.assertNotNull(paymentResponse, "Invalid response");
			Assert.assertNotNull(paymentResponse.getResponseCode(),
					"Invalid response code");

		} catch (ConnectionException e) {
			LoggerUtil.error(e.getMessage(), e);

		} catch (SDKException e) {
			LoggerUtil.error(e.getMessage(), e);

			Assert.fail(e.getMessage());
		}

	}

}