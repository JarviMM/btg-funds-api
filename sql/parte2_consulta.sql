-- ============================================================================
-- Parte 2 – SQL (20%)
-- Base de datos: BTG
-- ============================================================================
-- Consulta: Obtener los nombres de los clientes que tienen inscrito algún
-- producto disponible SOLO en las sucursales que visitan.
-- ============================================================================
-- Interpretación: Un cliente aparece en el resultado si tiene al menos un
-- producto inscrito cuyo conjunto de sucursales donde está disponible es un
-- subconjunto de las sucursales que el cliente visita.
-- Es decir, el producto no está disponible en ninguna sucursal que el
-- cliente NO visite.
-- ============================================================================

SELECT DISTINCT c.nombre
FROM Cliente c
INNER JOIN Inscripcion i ON c.id = i.idCliente
WHERE NOT EXISTS (
    -- Sucursales donde el producto está disponible
    -- que el cliente NO visita
    SELECT d.idSucursal
    FROM Disponibilidad d
    WHERE d.idProducto = i.idProducto
      AND d.idSucursal NOT IN (
          SELECT v.idSucursal
          FROM Visitan v
          WHERE v.idCliente = c.id
      )
);
