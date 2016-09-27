function y = lwlr(X_train, y_train, x_query, tau)

X = [ones(rows(X_train), 1) X_train]
x = [1; x_query]

% how close to zero we let the gradient get
threshold = 1e-9
lambda = 0.0001

% number training examples
m = rows(X) 
% number parameters
n = length(x)

theta = zeros(n, 1)

% tau_coefficient = (-1 / (2 * tau ^ 2))
% distances_from_x = x' - X
% norm_distances = sqrt(sum(abs(distances_from_x) .^ 2, 2))
% w = exp(tau_coefficient .* norm_distances .^ 2)
w = exp( ...
    (-1 / (2 * tau ^ 2)) ...
    .* sqrt(sum(abs(x' - X) .^ 2, 2)) ...
    .^ 2 ...
)

do
    predicted = h_theta(X', theta)

    % z = w .* (y_train - predicted)
    % gradient = X' * z - lambda .* theta
    gradient = X' * (w .* (y_train - predicted)) - lambda .* theta

    % D = diag(-1 .* w .* predicted .* (1 .- predicted))
    % hessian = X' * D * X - lambda .* eye(n)
    hessian = ...
        (X' * diag(-1 .* w .* predicted .* (1 .- predicted)) * X) ...
        - (lambda .* eye(n))

    theta = theta - (hessian \ gradient)

until (abs(gradient) < threshold)

prediction = h_theta(x, theta)
y = prediction > 0.5

endfunction

function predicted = h_theta(x, theta)

predicted = transpose(1 ./ (1 + exp(-1 .* theta' * x)))

endfunction
