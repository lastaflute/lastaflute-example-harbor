/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.mylasta.action;

import org.lastaflute.web.ruts.message.ActionMessages;

/**
 * The keys for message.
 * @author FreeGen
 */
public class HarborLabels extends ActionMessages {

    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    /** The key of the message: Member */
    public static final String LABELS_MEMBER = "{labels.member}";

    /** The key of the message: Product */
    public static final String LABELS_PRODUCT = "{labels.product}";

    /** The key of the message: Status */
    public static final String LABELS_STATUS = "{labels.status}";

    /** The key of the message: Withdrawal */
    public static final String LABELS_WITHDRAWAL = "{labels.withdrawal}";

    /** The key of the message: @[labels.member] @[labels.status] */
    public static final String LABELS_MEMBER_STATUS = "{labels.member.status}";

    /** The key of the message: @[labels.product] @[labels.status] */
    public static final String LABELS_PRODUCT_STATUS = "{labels.product.status}";

    /** The key of the message: @[labels.member] ID */
    public static final String LABELS_MEMBER_ID = "{labels.memberId}";

    /** The key of the message: @[labels.member] Name */
    public static final String LABELS_MEMBER_NAME = "{labels.memberName}";

    /** The key of the message: Mail Address */
    public static final String LABELS_EMAIL = "{labels.email}";

    /** The key of the message: Account */
    public static final String LABELS_ACCOUNT = "{labels.account}";

    /** The key of the message: Password */
    public static final String LABELS_PASSWORD = "{labels.password}";

    /** The key of the message: Version No */
    public static final String LABELS_VERSION_NO = "{labels.versionNo}";

    /** The key of the message: List */
    public static final String LABELS_LIST = "{labels.list}";

    /** The key of the message: Detail */
    public static final String LABELS_DETAIL = "{labels.detail}";

    /** The key of the message: Edit */
    public static final String LABELS_EDIT = "{labels.edit}";

    /** The key of the message: Add */
    public static final String LABELS_ADD = "{labels.add}";

    /** The key of the message: Search */
    public static final String LABELS_SEARCH = "{labels.search}";

    /** The key of the message: Register */
    public static final String LABELS_REGISTER = "{labels.register}";

    /** The key of the message: Update */
    public static final String LABELS_UPDATE = "{labels.update}";

    /** The key of the message: Confirm */
    public static final String LABELS_CONFIRM = "{labels.confirm}";

    /** The key of the message: Finish */
    public static final String LABELS_FINISH = "{labels.finish}";

    /** The key of the message: @[labels.list] of @[labels.member] */
    public static final String LABELS_MEMBER_LIST_TITLE = "{labels.member.list.title}";

    /** The key of the message: @[labels.add] @[labels.member] */
    public static final String LABELS_MEMBER_ADD_TITLE = "{labels.member.add.title}";

    /** The key of the message: @[labels.edit] @[labels.member] */
    public static final String LABELS_MEMBER_EDIT_TITLE = "{labels.member.edit.title}";

    /** The key of the message: input keyword to search */
    public static final String LABELS_MEMBER_INPUT_KEYWORD = "{labels.member.input.keyword}";

    /** The key of the message: e.g. 153-0051 */
    public static final String LABELS_MEMBER_INPUT_ZIP_CODE = "{labels.member.input.zipCode}";

    /** The key of the message: My Page */
    public static final String LABELS_MYPAGE_TITLE = "{labels.mypage.title}";

    /** The key of the message: Profile */
    public static final String LABELS_PROFILE_TITLE = "{labels.profile.title}";

    /** The key of the message: @[labels.withdrawal] @[labels.account] */
    public static final String LABELS_WITHDRAWAL_TITLE = "{labels.withdrawal.title}";

    /** The key of the message: @[labels.confirm] @[labels.withdrawal] Reason */
    public static final String LABELS_WITHDRAWAL_CONFIRM_TITLE = "{labels.withdrawal.confirm.title}";

    /** The key of the message: @[labels.list] of @[labels.product] */
    public static final String LABELS_PRODUCT_LIST_TITLE = "{labels.product.list.title}";

    /** The key of the message: @[labels.detail] of @[labels.product] */
    public static final String LABELS_PRODUCT_DETAIL_TITLE = "{labels.product.detail.title}";

    /** The key of the message: Sign In */
    public static final String LABELS_SIGNIN_TITLE = "{labels.signin.title}";

    /** The key of the message: Sign in */
    public static final String LABELS_SIGNIN_BUTTON = "{labels.signin.button}";

    /** The key of the message: input Pixy */
    public static final String LABELS_SIGNIN_INPUT_ACCOUNT = "{labels.signin.input.account}";

    /** The key of the message: input sea */
    public static final String LABELS_SIGNIN_INPUT_PASSWORD = "{labels.signin.input.password}";

    /** The key of the message: Notice */
    public static final String LABELS_ERROR_MESSAGE_TITLE = "{labels.error.message.title}";

    /** The key of the message: -- */
    public static final String LABELS_LISTBOX_CAPTION_SHORT = "{labels.listbox.caption.short}";

    /** The key of the message: ---- */
    public static final String LABELS_LISTBOX_CAPTION_LONG = "{labels.listbox.caption.long}";

    /** The key of the message: select */
    public static final String LABELS_LISTBOX_CAPTION_TELL = "{labels.listbox.caption.tell}";

    /**
     * Assert the property is not null.
     * @param property The value of the property. (NotNull)
     */
    protected void assertPropertyNotNull(String property) {
        if (property == null) {
            String msg = "The argument 'property' for message should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }
}
