function theta = l1ls(X,y,lambda)

n = columns(X)
theta = zeros(n, 1)

do 
    theta_new = theta(:)

    for i = 1:n

        X_i = X(:,i)

        theta_bar = theta_new(:)
        theta_bar(i) = 0

        partial_result = (-1 / (X_i' * X_i)) * ((1/2) * (theta_bar' * X' * X_i) + (1/2) * (X_i' * X * theta_bar) - (X_i' * y))
        theta_i_s1 = max(0, partial_result - (lambda / (X_i' * X_i)))
        theta_i_sn1 = min(0, partial_result + (lambda / (X_i' * X_i)))

        s1_objective_value = J_theta(X,X_i,theta_bar,theta_i_s1,y,lambda,1)
        sn1_objective_value = J_theta(X,X_i,theta_bar,theta_i_sn1,y,lambda,0)

        if (s1_objective_value < sn1_objective_value)
            theta_new(i) = theta_i_s1
        else
            theta_new(i) = theta_i_sn1
        endif
    endfor

    delta = abs(theta - theta_new)
    theta = theta_new

until (delta < .00001)

endfunction


function J = J_theta(X,X_i,theta_bar,theta_i,y,lambda,s)

J = (1/2) * (X * theta_bar + X_i * theta_i - y)' * (X * theta_bar + X_i * theta_i - y) + lambda * norm(theta_bar, 1) + lambda * s * theta_i

endfunction


