import React from 'react';
import OktaSignIn from '@okta/okta-signin-widget';

export default class LoginPage extends React.Component{
  constructor(){
    super();
    this.state = {user:null};
    this.widget = new OktaSignIn({
      baseUrl: 'https://dev-781536.oktapreview.com',
      clientId: 'TODO@OktaClientId',
      redirectUri: 'http://localhost:8080',
      authParams: {
        responseType: 'TODO@responseType'
      }
    });
  }

  componentDidMount(){
    this.widget.renderEl({el:this.loginContainer},
      (response) => {
        this.setState({user: response.claims.email});
      },
      (err) => {
        console.log(err); // eslint-disable-line no-console
      }
    );
  }

  render(){
    return (
     <div ref={ (div) => {this.loginContainer = div; } } />
    );
  }
}
