function W = ica(X)

    count = 1
    begin = time

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
            X_chunk = X_shuffled(start:min(stop, m), :)
            W = update_W(W, X_chunk)
            if (is_converged(W, old_W))
                break
            endif
        endfor
        count += 1
    until (is_converged(W, old_W))

    printf("took: %d  count: %d\n", time - begin, count)

endfunction


function new_W = update_W(W, X)

    alpha = 0.0005
    new_W = W + alpha * ((1 - 2 * sigmoid(W * X')) * X + inv(W'))

endfunction


function converged = is_converged(W, old_W)

    delta = abs(W - old_W)
    %converged = delta < .00001
    converged = delta < .001

endfunction


function a = sigmoid(z)

    a = 1.0 ./ (1.0 + exp(-z));

endfunction
