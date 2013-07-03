function y = lwlr(X_train, y_train, x, tau)

warning ("off", "Octave:broadcast");

min_delta = 0.0001;
lambda = 0.0001;

% set x_0 = 1 for all training examples
X_train = [ones(rows(X_train), 1), X_train];

% set x_0 = 1
x = [1; x];

m = rows(X_train); % number training examples
n = columns(X_train); % number parameters

% calculate weights
w = exp(((sum(abs(x' - X_train), 2)).^2) .* (-1 / (2*(tau^2))));

% initialize all n thetas to zero
theta = zeros(1, n)';


% loop til convergence:
delta = 1;

while delta > min_delta
  h_theta = (1 ./ (1 + exp(-theta' * X_train')))';

  z = w .* (y_train - h_theta);

  D = diag(-w .* h_theta .* (1 - h_theta));
  H = X_train' * D * X_train - lambda * eye(n);

  grad_l_theta = X_train' * z - lambda * theta;

  new_theta = theta - inv(H) * grad_l_theta;

  delta = abs(new_theta - theta);
  theta = new_theta;
endwhile

y = (1 ./ (1 + exp(-theta' * x))) > 0.5;
