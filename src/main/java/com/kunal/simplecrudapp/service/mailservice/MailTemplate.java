package com.kunal.simplecrudapp.service.mailservice;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public final class MailTemplate {
    private final String employeeCreatedSubject="Employee creation mail";
    private final String employeeUpdatedSubject="Employee updated mail";
    private final String employeeDeletedSubject="Employee deleted mail";

    private final String employeeCreatedMailTemplate="""
            <html>
              <body>
                <h2>Hi {{firstName}},</h2>
                <p>Your profile has been created successfully.</p>
                <p>Email: {{email}}</p>
                <br><br>
                <p>Thanks & Regards,</p>
                <p>HR Team</p>
              </body>
            </html>
    """;

    private final String employeeUpdatedMailTemplate= """
             <html>
              <body>
                <h2>Hi {{firstName}},</h2>
                <p>Your profile has been Updated successfully.</p>
                <p>Email: {{email}}</p>
            
                <br><br>
                <p>Thanks & Regards,</p>
                <p>HR Team</p>
              </body>
            </html>
            """;

    private final String employeeDeletedMailTemplate= """
             <html>
              <body>
                <h2>Hi {{firstName}},</h2>
                <p>Your profile has been Deleted successfully.</p>
                <p>Email: {{email}}</p>
            
                <br><br>
                <p>Thanks & Regards,</p>
                <p>HR Team</p>
              </body>
            </html>
            """;



}
