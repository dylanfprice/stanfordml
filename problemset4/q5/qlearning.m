function [q, steps_per_episode] = qlearning(episodes)

% set up parameters and initialize q values
alpha = 0.05
gamma = 0.99
num_states = 100
actions = [-1, 1]
q = zeros(num_states, length(actions))

steps_per_episode = zeros(episodes, 1)
state_top = num_states
rewards = -1 * ones(num_states, 1)
rewards(state_top) = 0

for i = 1:episodes
  [x, state, absorb] =  mountain_car([0.0 -pi/6], 0)

  while (state != state_top)
    action = choose_action(q, state)
    [new_x, new_state, new_absorb] = mountain_car(x, actions(action))
    steps_per_episode(i)++
    q(state, action) = (1 - alpha) * q(state, action) + gamma * (rewards(new_state) + gamma * max(q(new_state, :)))
  
    x = new_x
    state = new_state 
    absorb = new_absorb
  endwhile

endfor

endfunction


function action = choose_action(q, state)
  q_values = q(state, :)
  if (q_values == q_values(1))
    action = randi(length(q_values))
  else
    [max_q, action] = max(q_values)
  endif
endfunction
