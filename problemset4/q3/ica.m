function W = ica(X)

    X = X'
    [m, n] = size(X)
    W = eye(n, n)
    chunk_size = 100
    num_chunks = ceil(m / chunk_size)
    do
        old_W = W(:, :)
        X_shuffled = X(randperm(m), :)
        for i = 1:num_chunks
            stop = i * chunk_size
            start = stop - (chunk_size - 1)
            W = update_W(W, X_shuffled(start:min(stop, m), :))
            if (is_converged(W, old_W))
                break
            endif
        endfor
    until (is_converged(W, old_W))

endfunction


function new_W = update_W(W, X)

    alpha = 0.0005
    new_W = W + (alpha * ((1 - 2 * sigmoid(W * X')) * X + inv(W')))

endfunction


function converged = is_converged(W, old_W)

    converged = all(all(abs(W - old_W) < .0001))

endfunction


function a = sigmoid(z)

    a = 1.0 ./ (1.0 + exp(-z));

endfunction
