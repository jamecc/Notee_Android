/****************************************************************************
**
** Copyright (C) 2012 Coode Software
** Contact: http://www.coodesoft.com/
**
** This file is part of Notee.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
**
****************************************************************************/

package com.coodesoft.notee;

//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	private static final String SETTING_NOTEE = "notee.coodesoft.com";
	private static final String SETTING_AUTO_LOGIN = "auto_login";
	private static final String SETTING_USER_NAME = "user_name";
	private static final String SETTING_PASSWORD = "user_password";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask m_authTask = null;
    private NoteeNetworkReply m_pNetworkReply = new NoteeNetworkReply();
    private ProgressDialog m_progressDialog = null;

    // Values for email and password at the time of the login attempt.
    private String m_strUserName;
    private String m_strPassword;
    private String m_strEmail;
    private String m_strLoginToken;
    private String m_strUserId;
    private boolean m_bAutoLogin;

    private int m_nMode; // 0 login, 1 register, 2 forgot password
    private ConnectionDetector m_connectionDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        m_connectionDetector = new ConnectionDetector(this);
        
        Bundle extras = getIntent().getExtras();
        boolean bDontAutoLoginExtra = false;
        if(extras != null)
        	bDontAutoLoginExtra = extras.getBoolean("DONT_AUTO_LOGIN");
               
        m_nMode = 0;
        loadSettings();
        switchView();
        
        if(bDontAutoLoginExtra) {
        	if(m_bAutoLogin)
        	{
        		m_bAutoLogin = false;
        		m_strUserName = m_strPassword = "";
        		saveSettings();
        	}      	
        } else {
            // initial control
            if(m_bAutoLogin)
            {
            	CheckBox chkAutoLogin = (CheckBox)findViewById(R.id.chk_auto_login);
            	chkAutoLogin.setChecked(m_bAutoLogin);
            	EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            	editUserNameView.setText(m_strUserName);
            	EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            	editPasswordView.setText(m_strPassword);
            	attemptLogin();
            }
        }      
    }
    
    @Override
    public void onStop() {
    	if(m_progressDialog != null)
    		m_progressDialog.dismiss();
    	super.onStop();
    }
 
    private void switchView() {
    	// login
    	if(m_nMode == 0) {
            setContentView(R.layout.activity_login);
            
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            editUserNameView.requestFocus();
            
            // password
            EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            editPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            // sign in button
            findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            
            // text register
            findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				if(m_nMode == 0)
    				{
    					m_nMode = 1;
    					switchView();
    				}
    				else
    				{
    					m_nMode = 0;
    					switchView();
    				}				
    			}
    		});
            
            // text forgot password
            findViewById(R.id.btn_forgot_pwd).setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
					m_nMode = 2;
					switchView();			
    			}
    		});
    	}
    	// register
    	else if(m_nMode == 1) {
            setContentView(R.layout.activity_register);
            
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            editUserNameView.requestFocus();

            // Set up the login form.
            EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            editPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            
            // sign in button
            findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            
            // login
            findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				if(m_nMode == 0)
    				{
    					m_nMode = 1;
    					switchView();
    				}
    				else
    				{
    					m_nMode = 0;
    					switchView();
    				}			
    			}
    		});
            
            // text forgot password
            findViewById(R.id.btn_forgot_pwd).setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
					m_nMode = 2;
					switchView();			
    			}
    		});
        // forgot password
    	} else {
            setContentView(R.layout.activity_forgot_pwd);
            
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            editUserNameView.requestFocus();

            // sign in button
            findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            
            findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
					m_nMode = 0;
					switchView();	
    			}
    		});
    	}
    }
    
    private boolean verifyEmail(String strEmail) {
    	String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    	
        Pattern pattern = Pattern.compile(strPattern);
        Matcher m = pattern.matcher(strEmail);
        if (!m.matches()) {
        	return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_activity_login, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case R.id.item_exit:
        	finish();
            break;
        default:
        	break;
        }
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
		// check auto login
        CheckBox chkAutoLogin = (CheckBox)findViewById(R.id.chk_auto_login);
        if(chkAutoLogin != null) {
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            m_strUserName = editUserNameView.getText().toString().trim();
            m_strPassword = editPasswordView.getText().toString();            
	        m_bAutoLogin = chkAutoLogin.isChecked();
	        saveSettings();
        }
        
        if (m_authTask != null) {
            return;
        }
        
        if(!m_connectionDetector.isConnectingToInternet())
        {
        	Toast.makeText(this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
        	return;
        }
        
        // login
        if(m_nMode == 0)
        {
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            
            // Reset errors.
            editUserNameView.setError(null);
            editPasswordView.setError(null);

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password.
            if (TextUtils.isEmpty(m_strPassword)) {
            	editPasswordView.setError(getString(R.string.error_field_required));
                focusView = editPasswordView;
                cancel = true;
            } else if (m_strPassword.length() < 6) {
            	editPasswordView.setError(getString(R.string.str_error_email_length));
                focusView = editPasswordView;
                cancel = true;
            }

            // user name
            if (TextUtils.isEmpty(m_strUserName)) {
            	editUserNameView.setError(getString(R.string.error_field_required));
                focusView = editUserNameView;
                cancel = true;
            }
            
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                m_authTask = new UserLoginTask();
                m_authTask.execute((Void) null);
            }
        }
        // register
        else if(m_nMode == 1)
        {
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
            EditText editPasswordView2 = (EditText) findViewById(R.id.edit_password2);
            EditText editEmail = (EditText) findViewById(R.id.edit_email);
            
            // Reset errors.
            editUserNameView.setError(null);
            editPasswordView.setError(null);
            editPasswordView2.setError(null);
            editEmail.setError(null);

            // Store values at the time of the login attempt.
            String strPassword2 = editPasswordView2.getText().toString();
            m_strEmail = editEmail.getText().toString().trim();

            boolean cancel = false;
            View focusView = null;

            // password
            if (TextUtils.isEmpty(m_strPassword)) {
            	editPasswordView.setError(getString(R.string.error_field_required));
                focusView = editPasswordView;
                cancel = true;
            } else if (m_strPassword.length() < 6) {
            	editPasswordView.setError(getString(R.string.str_error_email_length));
                focusView = editPasswordView;
                cancel = true;
            }
            
            // confirm password
            if (TextUtils.isEmpty(strPassword2)) {
            	editPasswordView2.setError(getString(R.string.error_field_required));
                focusView = editPasswordView2;
                cancel = true;
            } else if(m_strPassword.compareTo(strPassword2) != 0) {
            	editPasswordView2.setError(getString(R.string.error_invalid_confirm_pwd));
                focusView = editPasswordView2;
                cancel = true;            	
            }

            // email
            if (TextUtils.isEmpty(m_strEmail)) {
            	editEmail.setError(getString(R.string.error_field_required));
                focusView = editEmail;
                cancel = true;
            } else if(!verifyEmail(m_strEmail)) {
            	editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;            	
            }
            
            // user name
            if (TextUtils.isEmpty(m_strUserName)) {
            	editUserNameView.setError(getString(R.string.error_field_required));
                focusView = editUserNameView;
                cancel = true;
            }
            
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                m_authTask = new UserLoginTask();
                m_authTask.execute((Void) null);
            }
            // forgot password
        } else {
            EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            editUserNameView.setError(null);
            m_strUserName = editUserNameView.getText().toString().trim();
            
            boolean cancel = false;
            View focusView = null;
            
            // user name
            if (TextUtils.isEmpty(m_strUserName)) {
            	editUserNameView.setError(getString(R.string.error_field_required));
                focusView = editUserNameView;
                cancel = true;
            }
            
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                m_authTask = new UserLoginTask();
                m_authTask.execute((Void) null);
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @TargetApi(Build.VERSION_CODES.FROYO)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
    	/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        */
        //mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    	

        
        if(show) {
        	if(m_progressDialog != null)
        		m_progressDialog.dismiss();
            m_progressDialog = new ProgressDialog(this) {
            	@Override
            	public void onStop() {
            		m_progressDialog = null;
            		if(m_authTask != null)
            			m_authTask.cancel(true);
            		m_authTask = null;
            	}
            };
            if(m_nMode == 0) {
            	m_progressDialog.setTitle(getString(R.string.str_login));
            } else if(m_nMode == 1) {
            	m_progressDialog.setTitle(getString(R.string.str_register));
            } else if(m_nMode == 2) {
            	m_progressDialog.setTitle("Send request to server");
            }
            m_progressDialog.setMessage("Please waiting..."); 
            
            m_progressDialog.show();
        }        	
        else if(m_progressDialog != null)
        	m_progressDialog.hide();
    }
    
    private void loadSettings() {
        // load setting
        SharedPreferences settings = getSharedPreferences(SETTING_NOTEE, 0);
        m_bAutoLogin = settings.getBoolean(SETTING_AUTO_LOGIN, false);
        m_strUserName = settings.getString(SETTING_USER_NAME, "");
        m_strPassword = settings.getString(SETTING_PASSWORD, "");
    }
    
    private void saveSettings() {
        SharedPreferences settings = getSharedPreferences(SETTING_NOTEE, 0);
       
        Editor editor = settings.edit();
        editor.putBoolean(SETTING_AUTO_LOGIN, m_bAutoLogin);
        if(m_bAutoLogin)
        {
            editor.putString(SETTING_USER_NAME, m_strUserName);
            editor.putString(SETTING_PASSWORD, m_strPassword);
        }
        else
        {
            editor.putString(SETTING_USER_NAME, "");
            editor.putString(SETTING_PASSWORD, "");
        }

        editor.commit();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
        	// login
        	if(m_nMode == 0)
        	{
            	if(m_pNetworkReply.login(m_strUserName, m_strPassword))
            	{
            		String strResultString = m_pNetworkReply.getResultString();
            		if(strResultString.compareTo("0") == 0)
            		{
            			m_strLoginToken = m_pNetworkReply.getValue("login_token");
            			m_strUserId = m_pNetworkReply.getValue("user_id");
            			return true;
            		}
            	}
        	}
        	// register
        	else if(m_nMode == 1)
        	{
            	if(m_pNetworkReply.register(m_strUserName, m_strPassword, m_strEmail))
            	{
            		String strResultString = m_pNetworkReply.getResultString();
            		if(strResultString.compareTo("0") == 0)
            		{
            			m_strLoginToken = m_pNetworkReply.getValue("login_token");
            			m_strUserId = m_pNetworkReply.getValue("user_id");
            			return true;
            		}
            	}
        	}
        	// forgot password
        	else
        	{
            	if(m_pNetworkReply.forgotPwd(m_strUserName))
            	{
            		String strResultString = m_pNetworkReply.getResultString();
            		if(strResultString.compareTo("0") == 0)
            		{
            			m_strEmail = m_pNetworkReply.getValue("email");
            			return true;
            		}
            	}
        	}

            return false;
        }

		@Override
        protected void onPostExecute(final Boolean success) {
			m_authTask = null;
            //showProgress(false);

            if (success) {
            	if(m_nMode == 0 || m_nMode == 1)
            	{
                    // switch to note list
    				Intent intent = new Intent(LoginActivity.this, NoteListActivity.class);
    				intent.putExtra("USER_ID", m_strUserId);
    				intent.putExtra("USER_NAME", m_strUserName);
    				intent.putExtra("LOGIN_TOKEN", m_strLoginToken);
    				startActivity(intent);
    				
    				finish();
            	} else {
            		m_nMode = 0;
            		switchView();
                    showProgress(false);
                    m_authTask = null;
                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.str_modify_pwd_info_link), m_strEmail), Toast.LENGTH_LONG).show();
            	}

            } else {
            	
            	String strResultString = m_pNetworkReply.getResultString();
            	
            	// login
            	if(m_nMode == 0)
            	{
                    EditText editPasswordView = (EditText) findViewById(R.id.edit_password);
                    
                    editPasswordView.setError(getString(R.string.error_incorrect_password));
                    editPasswordView.requestFocus();
            	}
            	// register
            	else if(m_nMode == 1)
            	{
                    EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
                    EditText editEmail = (EditText) findViewById(R.id.edit_email);
                    
            		// user name
            		if(strResultString.compareTo("1") == 0)
            		{
            			editUserNameView.setError(getString(R.string.str_exist_user_name));
            			editUserNameView.requestFocus();
            		}
            		// email
            		else if(strResultString.compareTo("2") == 0)
            		{
            			editEmail.setError(getString(R.string.str_exist_email));
            			editEmail.requestFocus();
            		}
            	}
            	// forgot password
            	else
            	{
            		EditText editUserNameView = (EditText) findViewById(R.id.edit_user_name);
            		// no user name
            		if(strResultString.compareTo("1") == 0)
            		{
            			editUserNameView.setError(getString(R.string.str_error_no_user_name));
            			editUserNameView.requestFocus();
            		}
            		// no email
            		else if(strResultString.compareTo("2") == 0)
            		{
            			editUserNameView.setError(getString(R.string.str_error_no_email));
            			editUserNameView.requestFocus();
            		}
            	}

                showProgress(false);
                m_authTask = null;
            }
        }

        @Override
        protected void onCancelled() {
        	m_authTask = null;
            showProgress(false);
        }
    }   
}
