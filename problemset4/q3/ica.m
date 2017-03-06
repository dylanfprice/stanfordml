function W = ica(X)

    X = X'
    [m, n] = size(X)
    W = eye(n, n)
    chunk_size = 100
    num_chunks = ceil(m / chunk_size)
    for j = 1:10
        old_W = W(:, :)
        X_shuffled = X(randperm(m), :)
        for i = 1:num_chunks
            stop = i * chunk_size
            start = stop - (chunk_size - 1)
            delta = compute_delta(W, X_shuffled(start:min(stop, m), :))
            endif
            W += delta
        endfor
    endfor

endfunction


function delta = compute_delta(W, X)

    alpha = 0.0005
    delta = alpha * ((1 - 2 * sigmoid(W * X')) * X + inv(W'))

endfunction


function a = sigmoid(z)

    a = 1.0 ./ (1.0 + exp(-z));

endfunction
