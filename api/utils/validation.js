const Joi = require("joi");

// Sign up
const signUpValidation = (data) => {
    const schema = Joi.object({
        name: Joi.string().min(1).max(255).required(),
        email: Joi.string().min(3).max(255).required().email(),
        password: Joi.string().min(6).max(255).required(),
    });
    return schema.validate(data);
};

// Sign in
const signInValidation = (data) => {
    const schema = Joi.object({
        email: Joi.string().min(3).max(255).required().email(),
        password: Joi.string().min(6).max(255).required(),
    });
    return schema.validate(data);
};

// Upload file
const uploadFileValidation = (data) => {
    const schema = Joi.object({
        path: Joi.string().min(1).max(260).required(),
    });
    return schema.validate(data);
};

// Create folder
const createFolderValidation = (data) => {
    const schema = Joi.object({
        name: Joi.string().min(1).max(255).required(),
        path: Joi.string().min(1).max(260).required(),
    });
    return schema.validate(data);
};

// Get folder
const getFolderValidation = (data) => {
    const schema = Joi.object({
        path: Joi.string().min(1).max(260).required(),
    });
    return schema.validate(data);
};

module.exports.signUpValidation = signUpValidation;
module.exports.signInValidation = signInValidation;
module.exports.uploadFileValidation = uploadFileValidation;
module.exports.createFolderValidation = createFolderValidation;
module.exports.getFolderValidation = getFolderValidation;
