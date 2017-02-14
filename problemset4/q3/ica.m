function W = ica(X)

    [n, m] = size(X)
    W = zeros(n, n)
    %do
        old_W = W(:, :)
        order = randperm(m)
        X_shuffled = X(:, order)
        
    %until (converged(W, old_W))
endfunction


function new_W = update_W(W, X_chunk)

    % placeholder
    new_W = W

endfunction


function a = converged(W, old_W)

    delta = abs(W - old_W)

    a = delta < .00001

endfunction


function a = sigmoid(z)

    a = 1.0 ./ (1.0 + exp(-z));

endfunction
