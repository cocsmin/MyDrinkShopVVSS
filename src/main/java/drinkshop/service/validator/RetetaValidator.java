package drinkshop.service.validator;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;

import java.util.List;

public class RetetaValidator implements Validator<Reteta> {

    @Override
    public void validate(Reteta reteta) {

        String errors = "";

        // C1: id invalid
        if (reteta.getId() <= 0)
            errors += "Product ID invalid!\n";

        // C2a + C2b: ingrediente null sau goale
        List<IngredientReteta> ingrediente = reteta.getIngrediente();
        if (ingrediente == null || ingrediente.isEmpty()) {
            errors += "Ingrediente empty!\n";
            if (!errors.isEmpty())
                throw new ValidationException(errors);
            return;
        }

        // C3: loop pe ingrediente
        for (IngredientReteta entry : ingrediente) {
            // C4: cantitate invalida
            if (entry.getCantitate() <= 0) {
                errors += "[" + entry.getDenumire() + "] cantitate negativa sau zero\n";
            }
        }

        // C5: arunca exceptie daca exista erori
        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}