/*
* Copyright 2010 Peter Backx, streamhead.com
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.streamhead.gae.paypal.ipn;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.streamhead.gae.paypal.ipn.IPNMessage.Builder;
import com.streamhead.gae.paypal.variable.PaymentStatus;
import com.streamhead.gae.paypal.variable.TransactionType;

public class IPNMessageParser {

	private static final Logger log = Logger.getLogger(IPNMessageParser.class.getName());
	
	private Map<String, String> nvp;
	private boolean validated = false;
	
	public IPNMessageParser(Map<String, String> nvp, boolean validated) {
		this.nvp = nvp;
		this.validated = validated;
	}

	public IPNMessage parse() {
		IPNMessage.Builder builder = new IPNMessage.Builder(nvp);
		if(validated)
			builder.validated();
		for(Map.Entry<String, String> param : nvp.entrySet()) {
			addVariable(builder, param);
		}
		return builder.build();
	}

	private void addVariable(Builder builder, Entry<String, String> param) {
		String name = param.getKey();
		String value = param.getValue();
		if(name.equals("txn_type")) {
			try {
				builder.transactionType(TransactionType.valueOf(value));
			} catch (IllegalArgumentException e) {
				// Unknown transaction type
				log.log(Level.SEVERE, "unknown txn_type received, please investigate", e);
			}
		} else if(name.equals("payment_status")) {
			try {
				builder.paymentStatus(PaymentStatus.valueOf(value));
			} catch (IllegalArgumentException e) {
				// Unknown payment status
				log.log(Level.SEVERE, "unknown payment_status received, please investigate", e);
			}
		} else if(name.equals("mc_gross")) {
			builder.mcGross(value);
		} else if(name.equals("mc_currency")) {
			builder.mcCurrency(value);
		} else if(name.equals("item_number")) {
			builder.itemNumber(value);
		} else if(name.equals("custom")) {
			builder.custom(value);
		} else if(name.equals("txn_id")) {
			builder.txnId(value);
		} else if(name.equals("subscr_id")) {
			builder.subscrId(value);
		}
	}

}
