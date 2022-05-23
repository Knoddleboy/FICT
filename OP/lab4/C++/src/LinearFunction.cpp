#include "../headers/LinearFunction.h"

// Override methods for linear function, since we do not need to manage quadratic coefficient

void LinearFunction::increase_coefficients(double scalar)
{
    m_linear_coeff += scalar;
    m_free_term += scalar;
}

void LinearFunction::decrease_coefficients(double scalar)
{
    m_linear_coeff -= scalar;
    m_free_term -= scalar;
}